package utils;

import classes.Student;

import java.util.*;

public class StudentGenerator {
    public static List<Student> generateStudents(int studentNo) {
        List<Student> studentList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < studentNo; i++) {
            UUID id = UUID.randomUUID();
            String name = "Name " + (i + 1);
            Date birthDate = new Date();
            int[] grades = new int[5];
            for (int j = 0; j < grades.length; j++) {
                grades[j] = random.nextInt(10) + 1;
            }
            UUID professorId = UUID.randomUUID();
            Student student = new Student(id, name, birthDate, grades, professorId);
            studentList.add(student);
        }
        return studentList;
    }
}
