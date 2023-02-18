package com.skytel.pigeon.web.requests;

import com.skytel.pigeon.validators.PasswordMatches;
import com.skytel.pigeon.validators.ValidEmail;
import com.skytel.pigeon.validators.ValidPassword;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class RegisterRequest {

        @NotNull
        @Size(min = 1, max = 20)
        private String firstname;

        @NotNull
        @Size(min = 1, max = 20)
        private String lastname;

        @NotNull
        @Size(min = 1, max = 20)
        private String company;

        @ValidEmail
        @NotNull
        @Size(min = 1)
        private String email;

        @NotNull
        @Size(min = 1, max = 20)
        private String phone;

        @ValidPassword
        private String password;

        @NotNull
        @Size(min = 1)
        private String matchingPassword;

        @NotNull
        @Size(min = 1)
        private String reference;

        @NotNull
        @Size(min = 1)
        private String postal;

        @NotNull
        @Size(min = 1)
        private String street;

        @NotNull
        @Size(min = 1)
        private String state;

        @NotNull
        @Size(min = 1)
        private String city;

        @NotNull
        @Size(min = 1)
        private String country;

        private boolean isUsing2FA;

        private Integer role;
}
