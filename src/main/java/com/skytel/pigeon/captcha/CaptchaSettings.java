package com.skytel.pigeon.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettings {

    private String site;
    private String secret;
}
