package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Data {
    public final static List<Exam> EXAMS = Arrays.asList(
            new Exam(5L, "Math mock"),
            new Exam(2L, "Languages mock"),
            new Exam(3L, "History mock")
    );

    public final static List<String> QUESTIONS = Arrays.asList(
        "Question 1",
        "Question 2",
        "Question 3",
        "Question 4",
        "Question 5"
    );
}