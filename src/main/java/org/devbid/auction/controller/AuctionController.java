package org.devbid.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.auction.dto.AuctionCreateRequest;
import org.devbid.product.application.ProductService;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.user.security.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {
    private final ProductService productService;

    @GetMapping("/{productId}/new")
    public String register(@PathVariable("productId") Long productId, @AuthenticationPrincipal AuthUser authUser, Model model) {
        Long sellerId = authUser.getId();
        ProductListResponse product = productService.findEditableByIdAndSeller(productId, sellerId);
        AuctionCreateRequest auctionCreateRequest = new AuctionCreateRequest(
                productId,
                null,
                null,
                null,
                null
        );

        model.addAttribute("product", product);
        model.addAttribute("auctionCreateRequest", auctionCreateRequest);
        return "auctions/new";
    }

}
