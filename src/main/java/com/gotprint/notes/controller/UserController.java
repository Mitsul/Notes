package com.gotprint.notes.controller;

import com.gotprint.notes.model.User;
import com.gotprint.notes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Mono<String>> createUser(@Valid @RequestBody User user, Errors err){
        if(err.hasErrors()) {
            return ResponseEntity.badRequest().body(Mono.just(Objects.requireNonNull(err.getFieldError()).getField() + " : " + err.getFieldError().getDefaultMessage()));
        } else {
            return ResponseEntity.ok(userService.createUser(user));
        }
    }
}
