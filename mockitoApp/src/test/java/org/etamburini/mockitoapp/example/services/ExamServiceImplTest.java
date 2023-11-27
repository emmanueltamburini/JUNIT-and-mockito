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

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {
    @Mock
    private ExamRepositoryImpl repository;
    @Mock
    private QuestionRepositoryImpl questionRepository;
    @InjectMocks
    private ExamServiceImpl service;
    @Captor
    private ArgumentCaptor<Long> captor;

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
            public Exam answer(InvocationOnMock invocation) {
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

    @Test
    void testHandleException() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenThrow(IllegalArgumentException.class);

         final Exception exception = assertThrows(IllegalArgumentException.class, () -> {
             service.findExamWithQuestionsByName("Math mock");
         });

         assertEquals(IllegalArgumentException.class, exception.getClass());

         verify(repository).findAll();
         verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testHandleExceptionWithNull() {
        when(repository.findAll()).thenReturn(Data.EXAMS_NULL_ID);
        when(questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class);

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamWithQuestionsByName("Math mock");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamWithQuestionsByName("Math mock");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg > 0L));
        verify(questionRepository).findQuestionsByExamId(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamWithQuestionsByName("Math mock");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(new ArgMatchersCustom()));
    }

    public static class ArgMatchersCustom implements ArgumentMatcher<Long> {
        private Long argument;

        @Override
        public boolean matches(final Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "ArgMatchersCustom custom message: + " +
                    argument +" must be not null and bigger than 0";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMS);

        service.findExamWithQuestionsByName("Math mock");

        final ArgumentCaptor<Long> currentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionsByExamId(currentCaptor.capture());

        assertEquals(5L, currentCaptor.getValue());
    }

    @Test
    void testArgumentCaptorWithAnnotation() {
        when(repository.findAll()).thenReturn(Data.EXAMS);

        service.findExamWithQuestionsByName("Math mock");

        final ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionsByExamId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        final Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);
        doThrow(IllegalArgumentException.class).when(questionRepository).saveQuestions(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
           service.save(exam);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        doAnswer(invocation -> {
           final Long id = invocation.getArgument(0);
           return id == 5L ? Data.QUESTIONS : null;
        }).when(questionRepository).findQuestionsByExamId(anyLong());

        final Exam exam = service.findExamWithQuestionsByName("Math mock");

        assertEquals(5L, exam.getId() );
        assertEquals("Math mock", exam.getName());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));

         verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testSaveExamWithIncrementalIDAndDoAnwser() {
        final Exam currentExam = new Exam(8L, "Physical mock"); //It is because if I use Data.EXAM, the id will increment and other tests could be failed
        //Given
        doAnswer(new Answer<Exam>(){
            private Long currentId = 9L;
            @Override
            public Exam answer(InvocationOnMock invocation) {
                final Exam exam = invocation.getArgument(0);
                exam.setId(currentId++);
                return exam;
            }
        }).when(repository).save(any(Exam.class));

        //When
        final Exam exam = service.save(currentExam);

        //Then
        assertNotNull(exam.getId());
        assertEquals(9L, exam.getId());
        assertEquals("Physical mock", exam.getName());

        verify(repository).save(any(Exam.class));
    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        doCallRealMethod().when(questionRepository).findQuestionsByExamId(anyLong());

        final Exam exam = service.findExamWithQuestionsByName("Math mock");

        assertEquals(5L, exam.getId() );
        assertEquals("Math mock", exam.getName());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Real Question 1"));
        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(new ArgMatchersCustom()));
    }


    @Test
    void testSpy() {
        final ExamRepository examRepository = spy(ExamRepositoryImpl.class);
        final QuestionRepository questionRepository = spy(QuestionRepositoryImpl.class);
        final ExamService examService = new ExamServiceImpl(examRepository, questionRepository);

        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS); //NOTE: if I use the method when in a spy, the real function will be called here
        doReturn(Data.QUESTIONS).when(questionRepository).findQuestionsByExamId(anyLong());

        final Exam exam = examService.findExamWithQuestionsByName("Math");

        assertEquals(5L, exam.getId());
        assertEquals("Math", exam.getName());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }
}