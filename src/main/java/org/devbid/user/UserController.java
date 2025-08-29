package org.devbid.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor    //생성자 자동생성
public class UserController {

    private final UserRegistrationService userRegistrationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public String register(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        User user = userMapper.toEntity(userDto);
        userRegistrationService.register(user, userDto.getPassword());

        redirectAttributes.addFlashAttribute("message", "success");

        return "redirect:/";
    }

}
