package com.gotprint.notes.service;

import com.gotprint.notes.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public interface UserService {

    Mono<String> createUser(User user);

    Optional<User> getUserByEmail(String email);
}
