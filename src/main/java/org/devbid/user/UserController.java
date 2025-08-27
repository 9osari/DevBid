package org.devbid.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor    //생성자 자동생성
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        userService.register(user);
        return "success";
    }
}
