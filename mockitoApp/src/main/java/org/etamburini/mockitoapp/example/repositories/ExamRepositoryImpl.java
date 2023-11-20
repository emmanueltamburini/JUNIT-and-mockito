package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.models.Exam;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(
                new Exam(5L, "Math mock"),
                new Exam(2L, "Languages mock"),
                new Exam(3L, "History mock")
        );
    }

    @Override
    public Exam save(Exam exam) {
        return exam;
    }
}
