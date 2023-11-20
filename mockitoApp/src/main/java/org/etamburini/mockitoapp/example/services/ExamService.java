package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.Exam;

import java.util.Optional;

public interface ExamService {
    Optional<Exam> getExamByName(final String name);
    Exam findExamWithQuestionsByName(final String name);
    Exam save(final Exam exam);
}
