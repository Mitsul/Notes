package com.gotprint.notes.serviceImpl;

import com.gotprint.notes.config.UserInfoDetails;
import com.gotprint.notes.model.User;
import com.gotprint.notes.repository.UserRepository;
import com.gotprint.notes.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService, UserDetailsService {

    Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("loadUserByUsername :: received request to load user by email : {}", email);

        return Mono.fromCallable(() -> getUserByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User Does Not Exist")))
                .flatMap(user -> Mono.just(new UserInfoDetails(user)))
                .block();
    }

    @Override
    public Mono<String> createUser(User user) {
        LOGGER.info("createUser :: received request to create user with mail id : {}",
                user.getEmail());
        return Mono.fromCallable(() -> getUserByEmail(user.getEmail())
                        .orElse(null))
                .flatMap(existingUser ->
                        Mono.just("User already present with provided mail id"))
                .switchIfEmpty(Mono.defer(() -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                    return Mono.just("User created successfully");
                }));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
