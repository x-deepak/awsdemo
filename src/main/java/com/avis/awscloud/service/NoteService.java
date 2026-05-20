package com.avis.awscloud.service;

import com.avis.awscloud.entity.Note;
import com.avis.awscloud.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository repo;

    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    public Note save(String message) {
        Note note = new Note();
        note.setMessage(message);
        return repo.save(note);
    }

    public List<Note> listRecent() {
        return repo.findTop50ByOrderByCreatedAtDesc();
    }
}
