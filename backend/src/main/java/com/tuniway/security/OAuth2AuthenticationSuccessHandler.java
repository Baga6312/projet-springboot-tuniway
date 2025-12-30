package com.tuniway.security;

import com.tuniway.jwt.JwtUtils;
import com.tuniway.model.User;
import com.tuniway.repository.UserRepository;
import com.tuniway.model.Client;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Value("${app.oauth2.redirect.uri:http://tuniway.me/oauth2/redirect}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        if (avatarUrl == null) {
            avatarUrl = oAuth2User.getAttribute("picture");
        }

        if (email == null || email.isBlank()) {
            String login = oAuth2User.getAttribute("login");
            if (login != null && !login.isBlank()) {
                email = login + "@github.oauth.tuniway.tn";
            } else {
                email = "user" + System.currentTimeMillis() + "@oauth.tuniway.tn";
            }
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty() && name != null && !name.isBlank()) {
            userOptional = userRepository.findByUsername(name);
        }

        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (avatarUrl != null && !avatarUrl.isBlank()) {
                user.setProfilePicture(avatarUrl);
                userRepository.save(user);
            }
        } else {
            user = new Client();
            user.setEmail(email);
            user.setUsername(name != null ? name : email.split("@")[0]);
            user.setPassword(passwordEncoder.encode("OAUTH_USER_" + System.currentTimeMillis()));

            if (avatarUrl != null && !avatarUrl.isBlank()) {
                user.setProfilePicture(avatarUrl);
            }

            userRepository.save(user);
        }

        String token = jwtUtils.generateJwtTokenForUser(user);

        // ✅ FIXED: Properly encode query parameters (especially username with spaces)
        String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .queryParam("username", user.getUsername())
                .queryParam("email", user.getEmail())
                .queryParam("role", user.getRole().name())
                .queryParam("id", user.getId())
                .queryParam("profilePicture", user.getProfilePicture() != null ? user.getProfilePicture() : "")
                .encode()  // ✅ This encodes spaces and special characters
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
