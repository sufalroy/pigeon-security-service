package com.skytel.pigeon.security.google2fa;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.io.Serial;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String verificationCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = request.getParameter("code");
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}