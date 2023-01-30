package com.skytel.pigeon.registration.request;

import com.skytel.pigeon.registration.validation.PasswordMatches;
import com.skytel.pigeon.registration.validation.ValidEmail;
import com.skytel.pigeon.registration.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class RegisterRequest {

        @NotBlank
        @Size(min = 1, max = 20)
        private String firstname;
        @NotBlank
        @Size(min = 1, max = 20)
        private String lastname;
        @NotBlank
        @Size(min = 1, max = 20)
        private String company;
        @ValidEmail
        @NotBlank
        @Size(min = 1)
        private String email;
        @NotBlank
        @Size(min = 1, max = 20)
        private String phone;
        @ValidPassword
        private String password;
        @NotBlank
        @Size(min = 1)
        private String matchingPassword;
        @NotBlank
        @Size(min = 1)
        private String reference;
        @NotBlank
        @Size(min = 1)
        private String postal;
        @NotBlank
        @Size(min = 1)
        private String street;
        @NotBlank
        @Size(min = 1)
        private String state;
        @NotBlank
        @Size(min = 1)
        private String city;
        @NotBlank
        @Size(min = 1)
        private String country;
        private boolean isUsing2FA;
        private Integer role;
}
