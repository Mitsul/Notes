package com.gotprint.notes.serviceImpl;

import com.gotprint.notes.model.Note;
import com.gotprint.notes.model.User;
import com.gotprint.notes.repository.NoteRepository;
import com.gotprint.notes.service.NoteService;
import com.gotprint.notes.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Component
public class NoteServiceImpl implements NoteService {

    Logger LOGGER = LoggerFactory.getLogger(NoteServiceImpl.class);

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserService userService;


    @Override
    public Mono<String> createNote(Note note, String mailId) {
        LOGGER.info("createNote :: received request to create note with title : {} and body : {}, for user : {}", note.getTitle(), note.getNote(), mailId);
        return Mono.fromCallable(() -> {
                    Optional<User> user = userService.getUserByEmail(mailId);
                    note.setUser(user.get());
                    noteRepository.save(note);
                    LOGGER.info("createNote :: note created successfully.");
                    return "Note saved successfully";
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Note> getAllNotesByEmail(String email) {
        LOGGER.info("getAllNotesByEmail :: received request to get all notes by email : {}", email);
        return Flux.defer(() -> {
            Optional<User> optionalUser = userService.getUserByEmail(email);
            if (optionalUser.isPresent()) {
                List<Note> notes = optionalUser.get().getNotes();
                LOGGER.info("getAllNotesByEmail :: found : {} notes for email : {}", notes.size(), email);
                return Flux.fromIterable(notes);
            } else {
                LOGGER.info("getAllNotesByEmail :: no notes found for email : {}", email);
                return Flux.empty();
            }
        });
    }

    @Override
    public Mono<Note> getNoteById(String email, Long id) {
        LOGGER.info("getNoteById :: received request to get note by email : {} and id : {}", email, id);
        return Mono.defer(() -> {
            Optional<User> optionalUser = userService.getUserByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Optional<Note> note = noteRepository.findByIdAndUserId(id, user.getId());
                return note.<Mono<? extends Note>>map(Mono::just).orElseGet(Mono::empty);
            } else {
                LOGGER.info("getNoteById :: no user found for email : {}", email);
                return Mono.empty();
            }
        });
    }

    @Override
    public Mono<String> deleteNoteById(String email, Long id) {
        LOGGER.info("deleteNoteById :: received request to delete note, id : {}, email :{}", id, email);
        return getNoteById(email, id)
                .flatMap(note -> {
                    noteRepository.deleteById(id);
                    LOGGER.info("deleteNoteById :: Note deleted successfully with id : {}, for email : {}", id, email);
                    return Mono.just("Note deleted successfully.");
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOGGER.info("deleteNoteById :: No note found for email: {} and id: {}", email, id);
                    return Mono.just("Note not found");
                }));
    }

    @Override
    public Mono<String> modifyNote(String email, Long id, Note updateNote) {
        LOGGER.info("modifyNote :: received request to update note, id : {}, email : {}, updatedNote : {}", email, id, updateNote);
        return getNoteById(email, id)
                .flatMap(note -> {
                    note.setTitle(updateNote.getTitle());
                    note.setNote(updateNote.getNote());
                    noteRepository.save(note);
                    LOGGER.info("modify :: Note updated successfully for id : {}", id);
                    return Mono.just("Note updated successfully.");
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOGGER.info("deleteNoteById :: No note found for email: {} and id: {}", email, id);
                    return Mono.just("Note not found");
                }));
    }
}
