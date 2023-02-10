package com.skytel.pigeon.security;

public interface ISecurityUserService {
    
    String validatePasswordResetToken(String token);
}