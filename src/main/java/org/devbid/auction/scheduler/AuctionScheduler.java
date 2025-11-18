package org.devbid.auction.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.devbid.auction.repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {
    private final AuctionRepository auctionRepository;
    /*private final SimpMessagingTemplate messagingTemplate;*/

    @Scheduled(fixedRate = 600000)  //10분마다 이 메서드 실행
    @Transactional
    public void updateAuctionStatus() {
        log.info("===== Update auction status Scheduler =====");
        LocalDateTime now = LocalDateTime.now();

        startAuctions(now);

        endAuctions(now);

        log.info("===== 경매 상태 업데이트 완료 =====");
    }

    private void startAuctions(LocalDateTime now) {
        List<Auction> auctionStart = auctionRepository.findByStatusAndStartTimeBefore(AuctionStatus.BEFORE_START, now);
        if(auctionStart.isEmpty()) {
            log.info("===== Noting auction start =====");
            return;
        }
        log.info("===== {} auction start =====", auctionStart.size());

        auctionStart.forEach(auction -> {
           try {
               auction.startAuction();      //상태 '경매중'
               log.info("===== Auction start: auctionid: {}, productname: {}",
                       auction.getId(), auction.getProduct().getProductName().getValue());
           } catch (Exception e) {
               log.error("===== Auction start Error: auctionId={}", auction.getId(), e);
           }
        });
    }

    private void endAuctions(LocalDateTime now) {
        List<Auction> auctionsEnd = auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ONGOING, now);
        if(auctionsEnd.isEmpty()) {
            log.info("===== Noting auction end =====");
            return;
        }
        log.info("===== {} auction end =====", auctionsEnd.size());
        auctionsEnd.forEach(auction -> {
            try {
                auction.endAuction();   //상태 "경매종료"
                log.info("===== Auction end: auctionid: {}, productname: {}",
                        auction.getId(), auction.getProduct().getProductName().getValue());
            } catch (Exception e) {
                log.error("===== Auction end Error: auctionId={}", auction.getId(), e);
            }
        });
    }
}
