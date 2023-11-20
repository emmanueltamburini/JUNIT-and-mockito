package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.models.Exam;

import java.util.List;

public interface QuestionRepository {
    List<String> findQuestionsByExamId(final Long id);
}
