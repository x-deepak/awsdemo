package com.avis.awscloud.repository;

import com.avis.awscloud.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findTop50ByOrderByCreatedAtDesc();
}
