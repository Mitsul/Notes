package com.gotprint.notes.controller;

import com.gotprint.notes.config.SecurityConfig;
import com.gotprint.notes.model.Note;
import com.gotprint.notes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;
    @Autowired
    private SecurityConfig securityConfig;

    @PostMapping("/create")
    private ResponseEntity<Mono<String>> createNote(@Valid @RequestBody Note note, Errors err) {
        if(err.hasErrors()) {
            return ResponseEntity.badRequest().body(Mono.just(
                    Objects.requireNonNull(err.getFieldError()).getField()
                            + " : "
                            + err.getFieldError().getDefaultMessage()));
        } else {
            return ResponseEntity.ok(noteService.createNote(note, securityConfig.getLoggedInUserDetails().getUsername()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<Flux<Note>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotesByEmail(securityConfig.getLoggedInUserDetails().getUsername()));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Mono<Note>> getNote(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(noteService.getNoteById(securityConfig.getLoggedInUserDetails().getUsername(), id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Mono<String>> deleteNote(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(noteService.deleteNoteById(securityConfig.getLoggedInUserDetails().getUsername(), id));
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<Mono<String>> modifyNote(@PathVariable(value = "id") Long id,
                                                   @RequestBody Note note, Errors err){
        if(err.hasErrors()) {
            return ResponseEntity.badRequest().body(Mono.just(
                    Objects.requireNonNull(err.getFieldError()).getField()
                            + " : " +
                            err.getFieldError().getDefaultMessage()));
        } else {
            return ResponseEntity.ok(noteService.modifyNote(securityConfig.getLoggedInUserDetails().getUsername(), id, note));
        }
    }
}
