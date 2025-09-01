package org.devbid.user.ui;

import lombok.RequiredArgsConstructor;
import org.devbid.user.application.command.UserRegistrationCommandHandler;
import org.devbid.user.application.command.UserRegistrationResult;
import org.devbid.user.domain.User;
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
    private final UserMapper userMapper;

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationRequest request, RedirectAttributes redirectAttributes) {
        User user = userMapper.toEntity(request);

        UserRegistrationResult result = commandHandler.handle(request.toCommand());

        // FlashAttribute로 뷰에 전달
        redirectAttributes.addFlashAttribute("message", "success");
        redirectAttributes.addFlashAttribute("user", UserRegistrationResponse.from(result));

        return "redirect:/";
    }

}
