package utils;

import classes.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileManager {
    public static void saveTxtStudents(String dir, List<Student> students) {
        try (PrintWriter file = new PrintWriter(dir)) {
            for (Student s : students) {
                file.printf("Id: %s, Name: %s, Date of birth: %s, No. grades: %d, Grades: %s\n", s.getId(), s.getName(), DateConverter.dateToString(s.getDateOfBirth()), s.getGrades().length, Arrays.toString(s.getGrades()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBinaryStudents(String dir, List<Student> students) {
        if (new File(dir).getParentFile() != null) {
            new File(dir).getParentFile().mkdirs();
        }
        try (DataOutputStream file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dir)))) {
            for (Student s : students) {
                file.writeUTF(s.getId().toString());
                file.writeUTF(s.getName());
                file.writeUTF(DateConverter.dateToString(s.getDateOfBirth()));
                file.writeInt(s.getGrades().length);
                for (int i = 0; i < s.getGrades().length; i++) {
                    file.writeInt(s.getGrade(i));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Student> readBinaryStudents(String dir) {
        List<Student> students = new ArrayList<>();
        try (DataInputStream file = new DataInputStream(new BufferedInputStream(new FileInputStream(dir)))) {
            while (file.available() > 0) {
                var id = UUID.fromString(file.readUTF());
                var name = file.readUTF();
                var dateOfBirth = DateConverter.stringToDate(file.readUTF());
                var noGrades = file.readInt();
                int[] grades = new int[noGrades];
                for (int i = 0; i < noGrades; i++) {
                    var grade = file.readInt();
                    grades[i] = grade;
                }
                Student student = new Student(id, name, dateOfBirth, grades);
                students.add(student);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return students;
    }
}
