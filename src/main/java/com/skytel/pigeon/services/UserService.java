package com.skytel.pigeon.services;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.skytel.pigeon.exceptions.UserAlreadyExistException;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.UserLocation;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.persistence.repository.RoleRepository;
import com.skytel.pigeon.persistence.repository.UserLocationRepository;
import com.skytel.pigeon.persistence.repository.UserRepository;
import com.skytel.pigeon.persistence.repository.VerificationTokenRepository;
import com.skytel.pigeon.web.requests.RegisterRequest;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.*;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    @Qualifier("GeoIPCountry")
    private DatabaseReader databaseReader;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private Environment environment;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Pigeon";

    @Override
    public User registerUser(final RegisterRequest request) {

        if (emailExists(request.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + request.getEmail());
        }

        final User user = new User();

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setCompany(request.getCompany());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // TODO: Encode Password.
        user.setReference(request.getReference());
        user.setPostal(request.getPostal());
        user.setStreet(request.getStreet());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setUsing2FA(request.isUsing2FA());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        return userRepository.save(user);
    }

    @Override
    public User getUser(final String verificationToken) {

        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }

        return null;
    }

    @Override
    public VerificationToken getVerificationToken(final String verificationToken) {

        return tokenRepository.findByToken(verificationToken);
    }

    @Override
    public void saveRegisteredUser(final User user) {

        userRepository.save(user);
    }

    @Override
    public void deleteUser(final User user) {

        final VerificationToken verificationToken = tokenRepository.findByUser(user);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        userRepository.delete(user);
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {

        final VerificationToken uToken = new VerificationToken(token, user);
        tokenRepository.save(uToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existVerifyToken) {

        VerificationToken vToken = tokenRepository.findByToken(existVerifyToken);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);

        return vToken;
    }

    @Override
    public User findUserByEmail(final String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByID(final long id) {
        return userRepository.findById(id);
    }

    @Override
    public String validateVerificationToken(String token) {

        final VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);

            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        userRepository.save(user);

        return TOKEN_VALID;
    }

    @Override
    public String generateQRUrl(User user) throws UnsupportedEncodingException {

        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME,
                user.getEmail(), user.getSecret(), APP_NAME), "UTF-8");
    }

    private boolean emailExists(final String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void addUserLocation(User user, String ip) {

        if (!isGeoIpLibEnabled()) {
            return;
        }

        try {

            final InetAddress ipAddress = InetAddress.getByName(ip);
            final String country = databaseReader.country(ipAddress)
                    .getCountry()
                    .getName();

            UserLocation location = new UserLocation(country, user);
            location.setEnabled(true);
            userLocationRepository.save(location);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isGeoIpLibEnabled() {

        return Boolean.parseBoolean(environment.getProperty("geo.ip.lib.enabled"));
    }

}
