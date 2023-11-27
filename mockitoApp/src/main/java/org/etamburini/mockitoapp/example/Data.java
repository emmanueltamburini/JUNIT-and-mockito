package org.etamburini.mockitoapp.example;

import org.etamburini.mockitoapp.example.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Data {
    public final static List<Exam> REAL_EXAMS = Arrays.asList(
            new Exam(5L, "Math"),
            new Exam(2L, "Languages"),
            new Exam(3L, "History")
    );
    public final static List<Exam> EXAMS = Arrays.asList(
            new Exam(5L, "Math mock"),
            new Exam(2L, "Languages mock"),
            new Exam(3L, "History mock")
    );

    public final static List<Exam> EXAMS_NULL_ID = Arrays.asList(
            new Exam(null, "Math mock"),
            new Exam(null, "Languages mock"),
            new Exam(null, "History mock")
    );

    public final static List<String> QUESTIONS = Arrays.asList(
        "Question 1",
        "Question 2",
        "Question 3",
        "Question 4",
        "Question 5"
    );

    public final static List<String> REAL_QUESTIONS = Arrays.asList(
            "Real Question 1",
            "Real Question 2",
            "Real Question 3",
            "Real Question 4",
            "Real Question 5"
    );

    public final static Exam EXAM = new Exam(8L, "Physical mock");

    public final static Exam REAL_EXAM = new Exam(8L, "Physical");
}
