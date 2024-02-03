package com.gotprint.notes.controller;

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

    @PostMapping("/create/{email}")
    private ResponseEntity<Mono<String>> createNote(@PathVariable(value = "email") String email,
                                                    @Valid @RequestBody Note note, Errors err) {
        if(err.hasErrors()) {
            return ResponseEntity.badRequest().body(Mono.just(
                    Objects.requireNonNull(err.getFieldError()).getField()
                            + " : "
                            + err.getFieldError().getDefaultMessage()));
        } else {
            return ResponseEntity.ok(noteService.createNote(note, email));
        }
    }

    @GetMapping("/get-all/{email}")
    public Flux<Note> getAllNotes(@PathVariable(value = "email") String email) {
        return noteService.getAllNotesByEmail(email);
    }

    @GetMapping("/get/{email}/{id}")
    public ResponseEntity<Mono<Note>> getNote(@PathVariable(value = "email") String email,
                                              @PathVariable(value = "id") Long id){
        return ResponseEntity.ok(noteService.getNoteById(email, id));
    }

    @DeleteMapping("/delete/{email}/{id}")
    public ResponseEntity<Mono<String>> deleteNote(@PathVariable(value = "email") String email,
                                                   @PathVariable(value = "id") Long id){
        return ResponseEntity.ok(noteService.deleteNoteById(email, id));
    }

    @PutMapping("/modify/{email}/{id}")
    public ResponseEntity<Mono<String>> modifyNote(@PathVariable(value = "email") String email,
                                                   @PathVariable(value = "id") Long id,
                                                   @RequestBody Note note, Errors err){
        if(err.hasErrors()) {
            return ResponseEntity.badRequest().body(Mono.just(
                    Objects.requireNonNull(err.getFieldError()).getField()
                            + " : " +
                            err.getFieldError().getDefaultMessage()));
        } else {
            return ResponseEntity.ok(noteService.modifyNote(email, id, note));
        }
    }
}
