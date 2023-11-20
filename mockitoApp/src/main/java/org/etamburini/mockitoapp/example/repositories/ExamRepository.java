package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.models.Exam;

import java.util.List;

public interface ExamRepository {
    List<Exam> findAll();

    Exam save(final Exam exam);
}
