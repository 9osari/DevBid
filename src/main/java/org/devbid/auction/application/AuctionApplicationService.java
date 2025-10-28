package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionFactory;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.repository.AuctionRepository;
import org.devbid.product.domain.Product;
import org.devbid.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuctionApplicationService implements AuctionService{
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;

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
}
