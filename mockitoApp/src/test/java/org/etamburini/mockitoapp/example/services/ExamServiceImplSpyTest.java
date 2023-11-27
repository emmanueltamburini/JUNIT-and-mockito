package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.Data;
import org.etamburini.mockitoapp.example.models.Exam;
import org.etamburini.mockitoapp.example.repositories.ExamRepository;
import org.etamburini.mockitoapp.example.repositories.ExamRepositoryImpl;
import org.etamburini.mockitoapp.example.repositories.QuestionRepository;
import org.etamburini.mockitoapp.example.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyTest {
    @Spy
    private ExamRepositoryImpl repository;
    @Spy
    private QuestionRepositoryImpl questionRepository;
    @InjectMocks
    private ExamServiceImpl service;

    @Test
    void testSpy() {
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS); //NOTE: if I use the method when in a spy, the real function will be called here
        doReturn(Data.QUESTIONS).when(questionRepository).findQuestionsByExamId(anyLong());

        final Exam exam = service.findExamWithQuestionsByName("Math");

        assertEquals(5L, exam.getId());
        assertEquals("Math", exam.getName());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }
}