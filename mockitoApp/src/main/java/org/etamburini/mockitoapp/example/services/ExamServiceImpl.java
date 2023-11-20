package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.Exam;
import org.etamburini.mockitoapp.example.repositories.ExamRepository;
import org.etamburini.mockitoapp.example.repositories.QuestionRepository;

import java.util.List;
import java.util.Optional;

public class ExamServiceImpl implements ExamService {
    private ExamRepository examRepository;

    private QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Exam> getExamByName(String name) {
        return examRepository.findAll().stream().filter(schoolTest -> schoolTest.getName().contains(name))
                .findFirst();
    }

    @Override
    public Exam findExamWithQuestionsByName(final String name) {
        final Optional<Exam> examOptional = getExamByName(name);
        Exam exam = null;
        if (examOptional.isPresent()) {
            exam = examOptional.orElseThrow();
           final List<String> questions = questionRepository.findQuestionsByExamId(exam.getId());
           exam.setQuestions(questions);
        }

        return exam;
    }

    @Override
    public Exam save(Exam exam) {
        if(!exam.getQuestions().isEmpty()) {
            questionRepository.saveQuestions(exam.getQuestions());
        }

        return examRepository.save(exam);
    }
}
