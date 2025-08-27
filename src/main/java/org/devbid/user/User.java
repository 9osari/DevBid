package org.devbid.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //pk를 JPA한테 알려줌, MySQL의 AUTO_INCREMENT 적용 (1,2,3,4,...)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)   //username VARCHAR(50) NOT NULL UNIQUE
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String phone;

    //@Column 안쓰면 기본값으로 nullable = true, unique = false, length = 255 (String일 경우)
    private LocalDateTime createdt;


    //@PrePersist, @PreUpdate로 자동 값
    @PrePersist
    protected void onCreate() {
        createdt = LocalDateTime.now();
    }

}
