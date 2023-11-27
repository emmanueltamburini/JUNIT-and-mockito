package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.Data;

import java.util.List;

public class QuestionRepositoryImpl implements QuestionRepository{
    @Override
    public List<String> findQuestionsByExamId(Long id) {
        return Data.REAL_QUESTIONS;
    }

    @Override
    public void saveQuestions(List<String> questions) {

    }
}
