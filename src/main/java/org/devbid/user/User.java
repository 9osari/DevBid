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
    @GeneratedValue(strategy = GenerationType.IDENTITY) //pk를 JPA한테 알려줌
    private Long userSn;

    @Column(nullable = false, unique = true, length = 50)   //user_id VARCHAR(50) NOT NULL UNIQUE
    private String userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDateTime createDt;
    private LocalDateTime updateDt;


    //@PrePersist, @PreUpdate로 자동 값
    @PrePersist
    protected void onCreate() {
        createDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDt = LocalDateTime.now();
    }

}
