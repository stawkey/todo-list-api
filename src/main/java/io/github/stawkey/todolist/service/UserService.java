package io.github.stawkey.todolist.service;

import io.github.stawkey.todolist.dto.UserDTO;
import io.github.stawkey.todolist.dto.request.RegisterRequest;
import io.github.stawkey.todolist.entity.User;
import io.github.stawkey.todolist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserRepository userRepository;
    public PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO save(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new IllegalStateException("Email is already registered");
        }

        User user = new User(
                registerRequest.name(),
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password())
        );

        logger.debug("Saving new user: {}", user.getEmail());
        userRepository.save(user);
        return UserDTO.convertToDTO(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        }

        return null;
    }

    public Integer getCurrentUserId() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getId();
    }
}
