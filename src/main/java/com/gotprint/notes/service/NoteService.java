package com.gotprint.notes.service;

import com.gotprint.notes.model.Note;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface NoteService {

    Mono<String> createNote(Note note, String mailId);

    Flux<Note> getAllNotesByEmail(String email);

    Mono<Note> getNoteById(String email, Long id);

    Mono<String> deleteNoteById(String email, Long id);

    Mono<String> modifyNote(String email, Long id, Note note);
}
