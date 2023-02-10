package com.skytel.pigeon.services;

import com.skytel.pigeon.persistence.models.NewLocationToken;
import com.skytel.pigeon.persistence.models.PasswordResetToken;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.web.requests.RegisterRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    public User registerUser(RegisterRequest request);

    public User getUser(String verificationToken);

    public void saveRegisteredUser(User user);

    public void deleteUser(User user);

    public void createVerificationTokenForUser(User user, String token);

    public VerificationToken getVerificationToken(String verificationToken);

    public VerificationToken generateNewVerificationToken(String token);

    public void createPasswordResetTokenForUser(User user, String token);

    public User findUserByEmail(String email);

    public PasswordResetToken getPasswordResetToken(String token);

    public Optional<User> getUserByPasswordResetToken(String token);

    public Optional<User> getUserByID(long id);

    public void changeUserPassword(User user, String password);

    public boolean checkIfValidOldPassword(User user, String password);

    public String validateVerificationToken(String token);

    public String generateQRUrl(User user) throws UnsupportedEncodingException;

    public User updateUser2FA(boolean use2FA);

    public List<String> getUsersFromSessionRegistry();

    public NewLocationToken isNewLoginLocation(String username, String ip);

    public String isValidNewLocationToken(String token);

    public void addUserLocation(User user, String ip);
}
