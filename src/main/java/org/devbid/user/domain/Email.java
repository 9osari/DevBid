package org.devbid.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
public class Email {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @Column(name = "email")
    private String value;

    protected Email() {
    }

    public Email(String value) {
        validateEmail(value);
        this.value = value;
    }

    private void validateEmail(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Email value cannot be null");
        }

        if(value.length() > 100) {
            throw new IllegalArgumentException("Email value cannot be greater than 100 characters");
        }

        if(!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email value is not valid");
        }
    }

    @Override
    public String toString() {
        return "Email{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Email username = (Email) object;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
