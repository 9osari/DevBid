package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionFactory;
import org.devbid.auction.domain.Bid;
import org.devbid.auction.domain.BidAmount;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.devbid.auction.infrastructure.cache.AuctionCacheManager;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.auction.repository.BidRepository;
import org.devbid.product.application.awsService.S3Service;
import org.devbid.product.domain.Product;
import org.devbid.product.domain.ProductImage;
import org.devbid.product.repository.ProductRepository;
import org.devbid.user.domain.User;
import org.devbid.user.repository.UserRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionApplicationService implements AuctionService{
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final AuctionDtoMapper auctionDtoMapper;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;
    private final AuctionCacheManager auctionCacheManager;
    private final RedissonClient redissonClient;

    @Override
    public void registerAuction(AuctionRegistrationRequest request,  Long sellerId) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        matchedSeller(sellerId, product);

        Auction auction = AuctionFactory.createFromPrimitives(
                product,
                request.startingPrice(),
                request.buyoutPrice(),
                request.startTime(),
                request.endTime()
        );
        auctionRepository.save(auction);
    }

    private static void matchedSeller(Long sellerId, Product product) {
        if(!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Seller id not matched");
        }
    }

    @Override
    public List<AuctionListResponse> findAllAuctions() {
        //여러 경매 각각에 대해 이미지 URL을 생성
        return auctionRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public Auction getAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("auction not found"));
    }

    public AuctionListResponse getAuctionDetail(Long auctionId) {
        //Redis에서 캐시 먼저 찾기
        AuctionListResponse cached = auctionCacheManager.getDetailRedis(auctionId);
        if(cached != null) {
            return cached;
        }

        //캐시 없으면 DB 조회
        Auction auction = getAuctionById(auctionId);
        AuctionListResponse response = convertToResponse(auction);

        //Redis에 저장
        auctionCacheManager.setDetailRedis(auctionId, response);

        return response;
    }

    private AuctionListResponse convertToResponse(Auction auction) {
        String mainImageUrl = getMainImageUrl(auction);
        List<String> subImageUrls = getSubImageUrls(auction);
        String winnerNickname = getWinnerNickname(auction);
        return auctionDtoMapper.toResponse(auction, winnerNickname, mainImageUrl, subImageUrls);
    }

    private String getWinnerNickname(Auction auction) {
        String winnerNickname = null;
        if (auction.getCurrentBidderId() != null) {
            winnerNickname = userRepository.findById(auction.getCurrentBidderId())
                    .orElseThrow(() -> new IllegalArgumentException("user not found"))
                    .getNickname().getValue();
        }
        return winnerNickname;
    }

    private String getMainImageUrl(Auction auction) {
        return auction.getProduct().getImages().stream()
                .filter(img -> img.getSortOrder() == 1)
                .findFirst()
                .map(img -> s3Service.buildPublicUrl(img.getImageKey()))
                .orElse(null);
    }

    private List<String> getSubImageUrls(Auction auction) {
        return auction.getProduct().getImages().stream()
                .filter(img -> img.getSortOrder() > 1)
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .map(img -> s3Service.buildPublicUrl(img.getImageKey()))
                .toList();
    }

    @Override
    @Transactional
    public BidPlacedEvent placeBid(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        RLock lock = getAuctionLock(auctionId);
        try {
            acquireLockOrThrow(lock);

            Auction auction = getAuctionById(auctionId);
            User bidder = getUserById(bidderId);

            //엔티티에 비즈니스 로직 위임
            Bid bid = auction.placeBid(bidder, new BidAmount(bidAmount));
            bidRepository.save(bid);

            //Redis 캐시 삭제 (업데이트 하면 DB 조회를 2번해서 비효율)
            auctionCacheManager.deleteDetailRedis(auctionId);
            return BidPlacedEvent.of(auctionId,
                    bidderId,
                    bid.getBidder().getNickname().getValue(),
                    bidAmount,
                    auction.getBidCount()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("입찰 처리가 중단되었습니다.", e);
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private RLock getAuctionLock(Long auctionId) {
        //락을 가져옴
        return redissonClient.getLock("auction:lock" + auctionId);
    }

    private static void acquireLockOrThrow(RLock lock) throws InterruptedException {
        //락을 못 얻으면 1초 후 예외, 락을 얻은 후 3초 유지
        boolean acquired = lock.tryLock(1000, 3000, TimeUnit.MILLISECONDS);
        if(!acquired) {
            throw new IllegalStateException("잠시 후 다시 시도해주세요.");
        }
    }

    private User getUserById(Long bidderId) {
        User bidder = userRepository.findById(bidderId) //사용자 찾기
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return bidder;
    }

    @Override
    @Transactional
    public BuyOutEvent buyOut(Long auctionId, Long buyerId) {
        RLock lock = getAuctionLock(auctionId);
        try{
            acquireLockOrThrow(lock);

            Auction auction = getAuctionById(auctionId);
            User buyer = getUserById(buyerId);

            Bid bid = auction.buyOut(buyer);
            bidRepository.save(bid);

            //Redis 캐시 삭제 (업데이트 하면 DB 조회를 2번해서 비효율)
            auctionCacheManager.deleteDetailRedis(auctionId);

            return BuyOutEvent.of(
                    auctionId,
                    buyerId,
                    buyer.getNickname().getValue(),
                    auction.getBuyoutPrice().getValue(),
                    bid.getAuction().getEndTime()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("즉시구매 처리가 중단되었습니다.", e);
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public <T> void publishDomainEvent(T event) {
        eventPublisher.publishEvent(event);
        log.info("입찰 이벤트 발행 - event: {}", event);
    }
}
