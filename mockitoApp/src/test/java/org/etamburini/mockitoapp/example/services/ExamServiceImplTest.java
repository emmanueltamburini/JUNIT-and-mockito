package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.Exam;
import org.etamburini.mockitoapp.example.repositories.ExamRepository;
import org.etamburini.mockitoapp.example.repositories.ExamRepositoryImpl;
import org.etamburini.mockitoapp.example.repositories.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {
    @Mock
    private ExamRepository repository;
    @Mock
    private QuestionRepository questionRepository;
    @InjectMocks
    private ExamServiceImpl service;

    @Test
    void getTestByName() {
        final ExamRepository realRepository = new ExamRepositoryImpl();
        final ExamService realService = new ExamServiceImpl(realRepository, questionRepository);
        final Optional<Exam> exam = realService.getExamByName("Math");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.orElseThrow().getId());
    }

    @Test
    void getTestByNameMock() {
        when(repository.findAll()).thenReturn(Data.EXAMS);

        final Optional<Exam> exam = service.getExamByName("Math mock");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.orElseThrow().getId());
    }

    @Test
    void getTestByNameInEmptyListMock() {
        final List<Exam> exams = Collections.emptyList();
        when(repository.findAll()).thenReturn(exams);
        final Optional<Exam> exam = service.getExamByName("Math mock");

        assertFalse(exam.isPresent());
        assertThrows(NoSuchElementException.class, () -> exam.orElseThrow().getId());
    }

    @Test
    void testQuestionsExam() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        final Exam exam = service.findExamWithQuestionsByName("Math mock");

        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));
    }

    @Test
    void testQuestionsExamVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        final Exam exam = service.findExamWithQuestionsByName("Math mock");

        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));
        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testNotExitsExamVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMS);

        final Exam exam = service.findExamWithQuestionsByName("Math mock 1");

        assertNull(exam);
        verify(repository).findAll();
    }

    @Test
    void testSaveExam() {
        when(repository.save(Data.EXAM)).thenReturn(Data.EXAM);

        final Exam exam = service.save(Data.EXAM);

        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physical mock", exam.getName());

        verify(repository).save(Data.EXAM);
    }

    @Test
    void testSaveExamWithQuestions() {
        final Exam examWithQuestions = Data.EXAM;
        examWithQuestions.setQuestions(Data.QUESTIONS);

        when(repository.save(any(Exam.class))).thenReturn(examWithQuestions);

        final Exam exam = service.save(Data.EXAM);

        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physical mock", exam.getName());

        verify(repository).save(any(Exam.class));
        verify(questionRepository).saveQuestions(anyList());
    }

    @Test
    void testSaveExamWithIncrementalID() {
        final Exam currentExam = new Exam(8L, "Physical mock"); //It is because if I use Data.EXAM, the id will increment and other tests could be failed
        //Given
        when(repository.save(currentExam)).then(new Answer<Exam>(){
            private Long currentId = 9L;
            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                final Exam exam = invocation.getArgument(0);
                exam.setId(currentId++);
                return exam;
            }
        });

        //When
        final Exam exam = service.save(currentExam);

        //Then
        assertNotNull(exam.getId());
        assertEquals(9L, exam.getId());
        assertEquals("Physical mock", exam.getName());

        verify(repository).save(currentExam);
    }
}