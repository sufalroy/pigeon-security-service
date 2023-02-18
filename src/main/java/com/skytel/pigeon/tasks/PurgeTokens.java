package com.skytel.pigeon.tasks;

import com.skytel.pigeon.persistence.repository.VerificationTokenRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@Transactional
public class PurgeTokens {

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired() {

        Date now = Date.from(Instant.now());

        tokenRepository.deleteAllExpiredSince(now);
    }
}
