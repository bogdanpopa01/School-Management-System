package utils;

import classes.Person;
import classes.Professor;
import classes.Student;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileManager {

    public static void saveTxt(String dir, List<Person> personList) {
        try (PrintWriter file = new PrintWriter(dir)) {
            for (Person p : personList) {
                if (p instanceof Student) {
                    Student student = (Student) p;
                    writeStudentTxt(file, student);
                } else if (p instanceof Professor) {
                    Professor professor = (Professor) p;
                    writeProfessorTxt(file, professor);
                } else {
                    throw new IllegalArgumentException("Unsupported person type: " + p.getClass().getName());
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBinary(String dir, List<Person> personList) {
        if (new File(dir).getParentFile() != null) {
            new File(dir).getParentFile().mkdirs();
        }
        try (DataOutputStream file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dir)))) {
            for (Person p : personList) {
                if (p instanceof Student) {
                    Student student = (Student) p;
                    writeStudentBinary(file, student);
                } else if (p instanceof Professor) {
                    Professor professor = (Professor) p;
                    writeProfessorBinary(file, professor);
                } else {
                    throw new IllegalArgumentException("Unsupported person type: " + p.getClass().getName());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Person> readBinaryStudents(String dir) {
        List<Person> personList = new ArrayList<>();
        try (DataInputStream file = new DataInputStream(new BufferedInputStream(new FileInputStream(dir)))) {
            while (file.available() > 0) {
                Student student = readBinaryStudent(file);
                personList.add(student);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return personList;
    }

    public static List<Person> readBinaryProfessors(String dir) {
        List<Person> personList = new ArrayList<>();
        try (DataInputStream file = new DataInputStream(new BufferedInputStream(new FileInputStream(dir)))) {
            while (file.available() > 0) {
                var id = UUID.fromString(file.readUTF());
                var name = file.readUTF();
                var dateOfBirth = DateConverter.stringToDate(file.readUTF());
                var taughtCourse = file.readUTF();
                Professor professor = new Professor(id, name, dateOfBirth, taughtCourse);
                var studentNo = file.readInt();
                for (int i = 0; i < studentNo; i++) {
                    Student student = readBinaryStudent(file);
                    professor.addStudent(student);
                }
                personList.add(professor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return personList;
    }

    private static void writeStudentTxt(PrintWriter file, Student student) {
        file.printf("Id: %s, Name: %s, Date of birth: %s, No. grades: %d, Grades: %s\n",
                student.getId(), student.getName(), DateConverter.dateToString(student.getDateOfBirth()),
                student.getGrades().length, Arrays.toString(student.getGrades()));
    }

    private static void writeProfessorTxt(PrintWriter file, Professor professor) {
        file.printf("Id: %s, Name: %s, Date of birth: %s, Taught course: %s\n",
                professor.getId(), professor.getName(), DateConverter.dateToString(professor.getDateOfBirth()), professor.getTaughtCourse());
        for (Map.Entry<UUID, Student> entry : professor.getStudentMap().entrySet()) {
            file.printf("\t");
            writeStudentTxt(file, entry.getValue());
        }
    }

    private static void writeStudentBinary(DataOutputStream file, Student student) {
        try {
            file.writeUTF(student.getId().toString());
            file.writeUTF(student.getName());
            file.writeUTF(DateConverter.dateToString(student.getDateOfBirth()));
            file.writeInt(student.getGrades().length);
            for (int i = 0; i < student.getGrades().length; i++) {
                file.writeInt(student.getGrade(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeProfessorBinary(DataOutputStream file, Professor professor) {
        try {
            file.writeUTF(professor.getId().toString());
            file.writeUTF(professor.getName());
            file.writeUTF(DateConverter.dateToString(professor.getDateOfBirth()));
            file.writeUTF(professor.getTaughtCourse());
            file.writeInt(professor.getStudentNo());
            for (Map.Entry<UUID, Student> entry : professor.getStudentMap().entrySet()) {
                writeStudentBinary(file, entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Student readBinaryStudent(DataInputStream file) {
        Student student = null;
        try {
            var id = UUID.fromString(file.readUTF());
            var name = file.readUTF();
            var dateOfBirth = DateConverter.stringToDate(file.readUTF());
            var noGrades = file.readInt();
            int[] grades = new int[noGrades];
            for (int i = 0; i < noGrades; i++) {
                var grade = file.readInt();
                grades[i] = grade;
            }
            student = new Student(id, name, dateOfBirth, grades);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (student != null) {
            return student;
        }
        throw new RuntimeException("The student in NULL!");
    }

}
