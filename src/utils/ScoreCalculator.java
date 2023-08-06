package utils;

import classes.Student;

import java.util.List;

public class ScoreCalculator {
    public static double calculateScore(List<Student> studentList) {
        return studentList.stream().mapToDouble(student -> {
            double sum = 0.0;
            int[] grades = student.getGrades();
            for (int grade : grades) {
                sum += grade;
            }
            return sum / grades.length;
        }).average().orElse(0.0);
    }
}
