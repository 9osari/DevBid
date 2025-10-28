package org.devbid.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.application.AuctionService;
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

@Slf4j
@Controller
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final ProductService productService;
    private final AuctionService auctionService;

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


}
