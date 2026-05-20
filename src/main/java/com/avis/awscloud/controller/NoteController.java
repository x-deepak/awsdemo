package com.avis.awscloud.controller;

import com.avis.awscloud.dto.NoteRequest;
import com.avis.awscloud.entity.Note;
import com.avis.awscloud.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<Note> create(@RequestBody NoteRequest req) {
        return ResponseEntity.ok(noteService.save(req.getMessage()));
    }

    @GetMapping
    public List<Note> list() {
        return noteService.listRecent();
    }
}
