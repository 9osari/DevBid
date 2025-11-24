package org.devbid.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.application.AuctionApplicationService;
import org.devbid.auction.application.AuctionService;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.devbid.product.application.ProductService;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.user.security.AuthUser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuctionController {

    private final ProductService productService;
    private final AuctionService auctionService;
    private final AuctionApplicationService auctionApplicationService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/auctionMain")
    public String auctionMain() {
        return "auctions/main";
    }

    @GetMapping("/auctions")
    public String auctions(Model model) {
        model.addAttribute("auctions", auctionService.findAllAuctions());
        return "auctions/list";
    }

    @GetMapping("/auctions/my")
    public String myAuctions(Model model,
                             @AuthenticationPrincipal AuthUser authUser,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionListResponse> auctionPage = auctionService.findAllAuctionsById(authUser.getId(), pageable);
        model.addAttribute("auctions", auctionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auctionPage.getTotalPages());
        model.addAttribute("totalItems", auctionPage.getTotalElements());
        return "auctions/myList";
    }

    @GetMapping("/auctions/{productId}")
    public String newAuction(@PathVariable("productId") Long productId, @AuthenticationPrincipal AuthUser authUser, Model model) {
        Long sellerId = authUser.getId();
        ProductListResponse product = productService.findEditableByIdAndSeller(productId, sellerId);
        AuctionRegistrationRequest auctionRegistrationRequest = new AuctionRegistrationRequest(
                productId,
                null,
                null,
                null,
                null
        );

        model.addAttribute("product", product);
        model.addAttribute("auctionCreateRequest", auctionRegistrationRequest);
        return "auctions/new";
    }
    
    @PostMapping("/auctions/{productId}")
    public String createAuction(@ModelAttribute("auctionCreateRequest") AuctionRegistrationRequest request,
                                @AuthenticationPrincipal AuthUser authUser,
                                BindingResult result,
                                RedirectAttributes ra,
                                Model model) {
        if(result.hasErrors()) {
            ProductListResponse product = productService.findEditableByIdAndSeller(request.productId(), authUser.getId());
            model.addAttribute("product", product);
            return "auctions/new";
        }
        Long sellerId = authUser.getId();
        AuctionRegistrationRequest auctionRegistrationRequest = new AuctionRegistrationRequest(
                request.productId(),
                request.startingPrice(),
                request.buyoutPrice(),
                request.startTime(),
                request.endTime()
        );
        auctionService.registerAuction(auctionRegistrationRequest,  sellerId);

        ra.addFlashAttribute("message", "경매가 성공적으로 등록되었습니다.");
        return "auctions/success";
    }

    @GetMapping("/auctions/{auctionId}/detail")
    public String detail(@PathVariable Long auctionId, Model model ) {
        AuctionListResponse dto = auctionApplicationService.getAuctionDetail(auctionId);
        model.addAttribute("auction", dto);
        return "auctions/detail";
    }

    @PostMapping("/auctions/bid/{auctionId}")
    public String bid(@PathVariable Long auctionId,
                      @RequestParam("bidAmount") BigDecimal bidAmount,
                      @AuthenticationPrincipal AuthUser authUser,
                      RedirectAttributes ra) {
        try {
            BidPlacedEvent event = auctionApplicationService.placeBid(auctionId, authUser.getId(), bidAmount);

            eventPublisher.publishEvent(event);

            ra.addFlashAttribute("eventType", "BID");
            ra.addFlashAttribute("message", "Bid Successfully!");
            return "redirect:/auctions/" + auctionId + "/success";
        } catch (IllegalArgumentException e) {
            log.error("입찰 실패 - auctionId: {}, error: {}", auctionId, e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auctions/" + auctionId + "/detail";
        }
    }

    @GetMapping("/auctions/{auctionId}/success")
    public String success(@PathVariable Long auctionId, Model model) {
        model.addAttribute("auctionId", auctionId);
        if(!model.containsAttribute("message")) {
            model.addAttribute("massage", "");
        }
        return "auctions/bid/success";
    }

    @PostMapping("/auctions/{auctionId}/buyout")
    public String buyout(@PathVariable Long auctionId,
                         @AuthenticationPrincipal AuthUser authUser,
                         RedirectAttributes ra) {
        try{
            BuyOutEvent event = auctionApplicationService.buyOut(auctionId, authUser.getId());

            eventPublisher.publishEvent(event);

            ra.addFlashAttribute("eventType", "BUYOUT");
            ra.addFlashAttribute("message", "Buyout Successfully!");
            return "redirect:/auctions/" + auctionId + "/success";
        } catch (IllegalArgumentException e) {
            log.error("Buyout fail - auctionId: {}, error: {}", auctionId, e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auctions/" + auctionId + "/detail";
        }
    }



}
