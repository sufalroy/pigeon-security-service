package com.skytel.pigeon.web.requests;

import com.skytel.pigeon.validators.ValidPassword;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Password {
    
    private String oldPassword;
    private String token;

    @ValidPassword
    private String newPassword;
}
