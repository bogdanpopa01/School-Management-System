package utils;

import classes.Person;
import classes.Professor;
import classes.Student;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileManager {

    private static void writeStudentTxt(PrintWriter file, Student student) {
        file.printf("Id: %s, Name: %s, Date of birth: %s, No. grades: %d, Grades: %s\n",
                student.getId(), student.getName(), DateConverter.dateToString(student.getDateOfBirth()),
                student.getGrades().length, Arrays.toString(student.getGrades()));
    }

    private static void writeProfessorTxt(PrintWriter file, Professor professor){
        file.printf("Id: %s, Name: %s, Date of birth: %s, Taught course: %s\n",
                professor.getId(), professor.getName(), DateConverter.dateToString(professor.getDateOfBirth()), professor.getTaughtCourse());
        for(Map.Entry<UUID,Student> entry : professor.getStudentMap().entrySet()){
            file.printf("\t");
            writeStudentTxt(file,entry.getValue());
        }
    }

    public static void saveTxt(String dir, List<Person> personList) {
        try (PrintWriter file = new PrintWriter(dir)) {
            for (Person p : personList) {
                if (p instanceof Student) {
                    Student student = (Student) p;
                    writeStudentTxt(file,student);
                } else if (p instanceof Professor) {
                    Professor professor = (Professor) p;
                    writeProfessorTxt(file,professor);
                } else {
                    throw new IllegalArgumentException("Unsupported person type: " + p.getClass().getName());
                }
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
