package org.devbid.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.application.AuctionApplicationService;
import org.devbid.auction.application.AuctionService;
import org.devbid.auction.dto.AuctionListResponse;
import org.devbid.auction.dto.AuctionRegistrationRequest;
import org.devbid.product.application.ProductService;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.user.security.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Slf4j
@Controller
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final ProductService productService;
    private final AuctionService auctionService;
    private final AuctionApplicationService auctionApplicationService;

    @GetMapping("/auctionMain")
    public String auctionMain() {
        return "auctions/auctionMain";
    }

    @GetMapping("/{productId}/new")
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
    
    @PostMapping("/{productId}/new")
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

    @GetMapping("/auctions")
    public String auctions(Model model) {
        model.addAttribute("auctions", auctionService.findAllAuctions());
        return "auctions/auctionList";
    }

    @GetMapping("/{auctionId}/detail")
    public String detail(@PathVariable Long auctionId, Model model ) {
        AuctionListResponse dto = auctionApplicationService.getAuctionDetail(auctionId);
        model.addAttribute("auction", dto);
        return "auctions/detail";
    }

    @PostMapping("/bid/{auctionId}")
    public String bid(@PathVariable Long auctionId,
                      @RequestParam("bidAmount") BigDecimal bidAmount,
                      @AuthenticationPrincipal AuthUser authUser,
                      RedirectAttributes ra) {
        try {
            auctionApplicationService.placeBid(auctionId, authUser.getId(), bidAmount);
            ra.addFlashAttribute("message", "Bid Successfully.");
            return "redirect:/auction/" + auctionId + "/success";
        } catch (IllegalArgumentException e) {
            log.error("입찰 실패 - auctionId: {}, error: {}", auctionId, e.getMessage(), e);
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auction/" + auctionId + "/detail";
        }
    }

    @GetMapping("/{auctionId}/success")
    public String success(@PathVariable Long auctionId, Model model) {
        model.addAttribute("auctionId", auctionId);
        if(!model.containsAttribute("message")) {
            model.addAttribute("massage", "");
        }
        return "auctions/bid/success";
    }


}
