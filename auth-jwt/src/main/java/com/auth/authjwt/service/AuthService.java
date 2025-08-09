package com.auth.authjwt.service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.authjwt.entity.RefreshToken;
import com.auth.authjwt.entity.Role;
import com.auth.authjwt.entity.User;
import com.auth.authjwt.repository.RefreshTokenRepository;
import com.auth.authjwt.repository.RoleRepository;
import com.auth.authjwt.repository.UserRepository;
import com.auth.authjwt.util.ApiResponse;
import com.auth.authjwt.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ApiResponse<Map<String, String>> register(String username, String rawPassword, String roleName) {
        if (userRepository.existsByUsername(username)) {
            return ApiResponse.error("USERNAME_TAKEN");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .enabled(true)
                .roles(Set.of(role))
                .build();
        user = userRepository.save(user);
        return ApiResponse.ok(Map.of("username", user.getUsername()));
    }

    public ApiResponse<Map<String, String>> login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshTokenValue = jwtUtil.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpMs()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return ApiResponse.ok(Map.of("accessToken", accessToken, "refreshToken", refreshTokenValue));
    }

    public ApiResponse<Map<String, String>> refresh(String refreshTokenValue) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expried or revoked");
        }

        User user = rt.getUser();
        // rotate refresh token
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        String newRefresh = jwtUtil.generateRefreshToken();
        RefreshToken newRt = RefreshToken.builder()
                .token(newRefresh).user(user).issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpMs())).revoked(false).build();
        refreshTokenRepository.save(newRt);

        String newAccess = jwtUtil.generateAccessToken(user);
        return ApiResponse.ok(Map.of("accessToken", newAccess, "refreshToken", newRefresh));
    }

    public void logout(String refreshTokenValue, String accessToken) {
        refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}
