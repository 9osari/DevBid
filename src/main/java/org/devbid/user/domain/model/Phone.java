package org.devbid.user.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phone {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
    private String value;

    public Phone(String value) {
        if(value == null || !PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("올바른 전화번호 형식이 아닙니다 (예: 010-1234-5678)");
        }
        this.value = value;
    }
}
