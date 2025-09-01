package org.devbid.user.ui;

import lombok.RequiredArgsConstructor;
import org.devbid.user.application.command.UserRegistrationCommandHandler;
import org.devbid.user.application.command.UserRegistrationResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor    //생성자 자동생성
public class UserRegistrationController {

    private final UserRegistrationCommandHandler commandHandler;

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationRequest request, RedirectAttributes redirectAttributes) {
        // Command 실행 → Result 반환
        UserRegistrationResult result = commandHandler.handle(request.toCommand());

        // Result → Response 변환
        UserRegistrationResponse response = UserRegistrationResponse.from(result);

        // FlashAttribute로 뷰에 전달
        redirectAttributes.addFlashAttribute("message", "success");
        redirectAttributes.addFlashAttribute("user", response);

        return "redirect:/";
    }

}
