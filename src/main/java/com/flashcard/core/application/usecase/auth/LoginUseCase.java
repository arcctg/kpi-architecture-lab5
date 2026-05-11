package com.flashcard.core.application.usecase.auth;

import com.flashcard.core.application.dto.AuthResult;
import com.flashcard.core.application.port.PasswordEncoder;
import com.flashcard.core.application.port.TokenProvider;
import com.flashcard.core.domain.error.DomainError;
import com.flashcard.core.domain.model.User;
import com.flashcard.core.domain.repository.UserRepository;
import com.flashcard.core.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public LoginUseCase(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public AuthResult execute(String email, String password) {
        Email emailVo = new Email(email);

        User user = userRepository.findByEmail(emailVo)
                .orElseThrow(() -> new DomainError("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new DomainError("Invalid credentials");
        }

        String token = tokenProvider.generateToken(emailVo.value());
        return new AuthResult(token);
    }
}
