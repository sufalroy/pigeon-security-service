package com.skytel.pigeon.captcha;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.skytel.pigeon.exceptions.ReCaptchaInvalidException;
import com.skytel.pigeon.exceptions.ReCaptchaUnavailableException;

@Service("captchaService")
public class CaptchaService extends AbstractCaptchaService {

    private final static Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    @Override
    public void processResponse(final String response) {

        securityCheck(response);

        final URI verifyUri = URI
                .create(String.format(RECAPTCHA_URL_TEMPLATE, getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            logger.debug("Google's response: {} ", googleResponse.toString());

            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }
        } catch (RestClientException rce) {
            throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.",
                    rce);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }
}
