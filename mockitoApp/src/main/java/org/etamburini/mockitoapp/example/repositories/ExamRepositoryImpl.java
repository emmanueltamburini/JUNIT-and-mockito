package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.Data;
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

        return Data.REAL_EXAMS;
    }

    @Override
    public Exam save(Exam exam) {
        return Data.REAL_EXAM;
    }
}
