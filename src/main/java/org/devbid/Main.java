package org.devbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        /*BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newHash = encoder.encode("1212");
        System.out.println("새로운 해시: " + newHash);*/

        SpringApplication.run(Main.class, args);
    }
}