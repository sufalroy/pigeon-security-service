package com.skytel.pigeon.services;

import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.web.requests.RegisterRequest;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface IUserService {

    public User registerUser(RegisterRequest request);

    public User getUser(String verificationToken);

    public void saveRegisteredUser(User user);

    public void deleteUser(User user);

    public void createVerificationTokenForUser(User user, String token);

    public VerificationToken getVerificationToken(String VerificationToken);

    public VerificationToken generateNewVerificationToken(String token);

    public User findUserByEmail(String email);

    public Optional<User> getUserByID(long id);

    public String validateVerificationToken(String token);

    public String generateQRUrl(User user) throws UnsupportedEncodingException;

    public void addUserLocation(User user, String ip);
}
