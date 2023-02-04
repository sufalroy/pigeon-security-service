package com.skytel.pigeon.web.events;

import com.skytel.pigeon.persistence.models.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String url;
    private final Locale locale;
    private final User user;

    public OnRegistrationCompleteEvent(final User user, final Locale locale, final String url) {

        super(user);
        this.user = user;
        this.locale = locale;
        this.url = url;
    }
}
