package io.github.stawkey.todolist.service;

import io.github.stawkey.todolist.dto.UserDTO;
import io.github.stawkey.todolist.dto.request.RegisterRequest;
import io.github.stawkey.todolist.entity.User;
import io.github.stawkey.todolist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserRepository userRepository;
    public PasswordEncoder passwordEncoder;

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
}
