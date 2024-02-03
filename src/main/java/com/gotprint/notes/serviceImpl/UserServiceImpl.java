package com.gotprint.notes.serviceImpl;

import com.gotprint.notes.model.User;
import com.gotprint.notes.repository.UserRepository;
import com.gotprint.notes.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<String> createUser(User user) {
        LOGGER.info("createUser :: received request to create user with mail id : {}",
                user.getEmail());
        return Mono.fromCallable(() -> getUserByEmail(user.getEmail())
                        .orElse(null))
                .flatMap(existingUser ->
                        Mono.just("User already present with provided mail id"))
                .switchIfEmpty(Mono.defer(() -> {
                    userRepository.save(user);
                    return Mono.just("User created successfully");
                }));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
