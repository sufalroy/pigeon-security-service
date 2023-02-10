package com.skytel.pigeon.security.location;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.skytel.pigeon.persistence.models.NewLocationToken;

import lombok.Getter;

@Getter
public class OnDifferentLocationLoginEvent extends ApplicationEvent {

    private final Locale locale;
    private final String username;
    private final String ip;
    private final NewLocationToken token;
    private final String url;


    public OnDifferentLocationLoginEvent(Locale locale, String username, String ip, NewLocationToken token, String url) {
        super(token);
        this.locale = locale;
        this.username = username;
        this.ip = ip;
        this.token = token;
        this.url = url;
    }
}
