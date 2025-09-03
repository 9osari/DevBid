package org.devbid.user.ui;

import lombok.RequiredArgsConstructor;
import org.devbid.user.application.command.UserRegistrationCommand;
import org.devbid.user.application.command.UserRegistrationCommandHandler;
import org.devbid.user.application.UserRegistrationResult;
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
        UserRegistrationCommand command = userMapper.toCommand(request);
        UserRegistrationResult result = commandHandler.registerUser(command);
        UserRegistrationResponse response = userMapper.toResponse(result);

        redirectAttributes.addFlashAttribute("message", "회원가입 성공: " + response.getUsername());

        return "redirect:/";
    }

}
