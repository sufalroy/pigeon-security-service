package com.skytel.pigeon.security.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import com.skytel.pigeon.exceptions.UnusualLocationException;
import com.skytel.pigeon.persistence.models.NewLocationToken;
import com.skytel.pigeon.services.IUserService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DifferentLocationChecker implements UserDetailsChecker {
    
    @Autowired
    private IUserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void check(UserDetails userDetails) {
        
        final String ip = getClientIP();

        final NewLocationToken token = userService.isNewLoginLocation(userDetails.getUsername(), ip);
        if(token != null) {
            final String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnDifferentLocationLoginEvent(request.getLocale(), userDetails.getUsername(), ip, token, url));

            throw new UnusualLocationException("unusual location");
        }
    }

    private String getClientIP() {

        final String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }
}
