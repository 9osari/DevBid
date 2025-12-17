package org.devbid.auction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.application.AuctionFacade;
import org.devbid.auction.application.AuctionService;
import org.devbid.auction.dto.AuctionEditRequest;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.auction.dto.BidPlacedEvent;
import org.devbid.auction.dto.BuyOutEvent;
import org.devbid.product.application.ProductService;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.user.security.AuthUser;
import org.devbid.user.security.oauth2.CustomOAuth2User;
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
    private final AuctionFacade auctionFacade;
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
                             @AuthenticationPrincipal CustomOAuth2User authUser,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionListResponse> auctionPage = auctionService.findAllAuctionsById(authUser.getUser().getId(), pageable);
        model.addAttribute("auctions", auctionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auctionPage.getTotalPages());
        model.addAttribute("totalItems", auctionPage.getTotalElements());
        return "auctions/myList";
    }

    @GetMapping("/auctions/{productId}")
    public String newAuction(@PathVariable("productId") Long productId, @AuthenticationPrincipal CustomOAuth2User authUser, Model model) {
        Long sellerId = authUser.getUser().getId();
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
                                @AuthenticationPrincipal CustomOAuth2User authUser,
                                BindingResult result,
                                RedirectAttributes ra,
                                Model model) {
        if(result.hasErrors()) {
            ProductListResponse product = productService.findEditableByIdAndSeller(request.productId(), authUser.getUser().getId());
            model.addAttribute("product", product);
            return "auctions/new";
        }
        Long sellerId = authUser.getUser().getId();
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

    @GetMapping("/auctions/{id}/edit")
    public String editAuction(@PathVariable Long id,
                              Model model) {
        AuctionListResponse auction = auctionService.getAuctionDetail(id);
        AuctionEditRequest editRequest = new AuctionEditRequest(
                id,
                auction.startingPrice(),
                auction.buyoutPrice(),
                auction.startTime(),
                auction.endTime()
        );

        model.addAttribute("auction", auction);
        model.addAttribute("auctionEditRequest", editRequest);
        return "auctions/edit";
    }

    @PostMapping("/auctions/{id}/edit")
    public String updateAuction(@PathVariable Long id,
                                @AuthenticationPrincipal CustomOAuth2User authUser,
                                @Valid @ModelAttribute("auctionEditRequest") AuctionEditRequest request,
                                BindingResult result) {
        if(result.hasErrors()) {
            return "auctions/edit";
        }
        auctionService.updateAuction(id, request, authUser.getUser().getId());
        return "redirect:/auctions/my";
    }

    @GetMapping("/auctions/{auctionId}/detail")
    public String detail(@PathVariable Long auctionId, Model model ) {
        AuctionListResponse dto = auctionService.getAuctionDetail(auctionId);
        model.addAttribute("auction", dto);
        return "auctions/detail";
    }

    @PostMapping("/auctions/bid/{auctionId}")
    public String bid(@PathVariable Long auctionId,
                      @RequestParam("bidAmount") BigDecimal bidAmount,
                      @AuthenticationPrincipal CustomOAuth2User authUser,
                      RedirectAttributes ra) {
        try {
            BidPlacedEvent event = auctionFacade.placeBid(auctionId, authUser.getUser().getId(), bidAmount);

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

    @PostMapping("/auctions/{auctionId}/buyout")
    public String buyout(@PathVariable Long auctionId,
                         @AuthenticationPrincipal CustomOAuth2User authUser,
                         RedirectAttributes ra) {
        try{
            BuyOutEvent event = auctionFacade.buyOut(auctionId, authUser.getUser().getId());

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

    @GetMapping("/auctions/{auctionId}/success")
    public String success(@PathVariable Long auctionId, Model model) {
        model.addAttribute("auctionId", auctionId);
        if(!model.containsAttribute("message")) {
            model.addAttribute("massage", "");
        }
        return "auctions/bid/success";
    }

    @PostMapping("auction/{id}")
    public String deleteAuction(@PathVariable Long id,
                                @AuthenticationPrincipal CustomOAuth2User authUser) {
        AuctionListResponse currentAuction = auctionService.getAuctionDetail(id);
        if (currentAuction == null) {
            throw new IllegalArgumentException("Auction not found");
        }
        auctionService.deleteAuction(id, authUser.getUser().getId());
        return "redirect:/auctions/my";
    }

}
