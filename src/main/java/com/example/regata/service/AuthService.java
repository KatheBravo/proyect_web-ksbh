package com.example.regata.service;

import com.example.regata.web.dto.RegisterRequest;
import com.example.regata.web.dto.LoginRequest;
import com.example.regata.web.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
