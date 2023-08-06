package classes;

import exceptions.NoStudentException;
import interfaces.IPassed;
import utils.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws NoStudentException {


        int[] gradesPool1 = new int[5];
        int[] gradesPool2 = new int[5];
        int[] gradesPool3 = new int[5];

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            gradesPool1[i] = random.nextInt(10) + 1;
            gradesPool2[i] = random.nextInt(10) + 1;
            gradesPool2[i] = random.nextInt(10) + 1;
        }

        // use of an abstract class
        System.out.println("Use of an abstract class:");
        Person person = new Student(UUID.randomUUID(), "Gigi", new Date(), gradesPool1);
        person.workToDo();

        // child member
        // demonstration of an interface, with static and default methods
        Student student1 = new Student(UUID.randomUUID(), "Florian", DateConverter.stringToDate("2002-01-30"), gradesPool1);
        Student student2 = new Student(UUID.randomUUID(), "Dorin", DateConverter.stringToDate("2003-01-30"), gradesPool2);
        Student student3 = new Student(UUID.randomUUID(), "Catalin", DateConverter.stringToDate("2004-01-30"), gradesPool3);

        System.out.println("\nI am a student:");
        System.out.println(student1);
        student1.wage();
        student1.workToDo();
        System.out.println("Has student1 passed? " + (IPassed.isPassed(student1.getGrades()) ? "Yes" : "No"));

        // use of cloneable interface for deep-copy
        System.out.println("\nUse of cloneable interface for deep-copy:");
        Student student4 = null;
        try {
            student4 = student1.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(student4);
        student4.setGrades(gradesPool2);
        student4.setName("Florian-->Bogdan");
        System.out.println(student4);
        System.out.println(student1);

        // another child member
        Professor professor1 = new Professor(UUID.randomUUID(), "Mihail", DateConverter.stringToDate("1956-10-20"), "PAW");

        System.out.println("\nI am a professor:");
        System.out.println(professor1);
        professor1.wage();
        professor1.workToDo();

        professor1.addStudent(student1);
        professor1.addStudent(student2);
        professor1.addStudent(student3);

        // custom exception
//        System.out.println(professor1.getStudent(UUID.randomUUID()));

        // file management
        Professor professor2 = new Professor(UUID.randomUUID(), "Yani", DateConverter.stringToDate("1956-01-19"), "SGBD");

        professor2.addStudent(student1);
        professor2.addStudent(student2);

        List<Person> orderedStudents = new ArrayList<>();
        for (Map.Entry<UUID, Student> entry : professor1.getStudentMap().entrySet()) {
            orderedStudents.add(entry.getValue());
        }
        Collections.sort(orderedStudents, new Comparator<>() {
            @Override
            public int compare(Person o1, Person o2) {
                OptionalDouble mean1 = Arrays.stream(((Student) o1).getGrades()).average();
                OptionalDouble mean2 = Arrays.stream(((Student) o2).getGrades()).average();
                return Double.compare(mean2.getAsDouble(), mean1.getAsDouble());
            }
        });

        List<Person> professorList = new ArrayList<>();
        professorList.add(professor1);
        professorList.add(professor2);

        // use of threads with file management

        Runnable saveTxtRunnable = () -> {
            FileManager.saveTxt("src\\files\\students.txt", orderedStudents);
            FileManager.saveTxt("src\\files\\professors.txt", professorList);
        };

        Runnable saveBinaryRunnable = () -> {
            FileManager.saveBinary("src\\files\\students.dat", orderedStudents);
            FileManager.saveBinary("src\\files\\professors.dat", professorList);
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(saveTxtRunnable);
        executorService.execute(saveBinaryRunnable);

        executorService.shutdown();

        List<Person> readStudents = FileManager.readBinaryStudents("src\\files\\students.dat");
        FileManager.saveTxt("src\\files\\readStudents.txt", readStudents);

        List<Person> readProfessors = FileManager.readBinaryProfessors("src\\files\\professors.dat");
        FileManager.saveTxt("src\\files\\readProfessors.txt", readProfessors);

        // assigning students to professors using threads
        List<Student> randomlyGeneratedStudents = StudentGenerator.generateStudents(100);
        ConcurrentLinkedQueue<Student> unassignedStudents = new ConcurrentLinkedQueue<>(randomlyGeneratedStudents);

        Thread assignmentThread1 = new Thread(() -> {
            StudentAssigner.assignStudentToProfessor(professor1, unassignedStudents);
        });
        Thread assignmentThread2 = new Thread(() -> {
            StudentAssigner.assignStudentToProfessor(professor2, unassignedStudents);
        });

        assignmentThread1.start();
        assignmentThread2.start();

        try {
            assignmentThread1.join();
            assignmentThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nNumber of students assigned:");
        System.out.println(professor1);
        System.out.println(professor2);

        // use of streams to calculate the average score of students from each professor

        List<Student> studentPool1 = new ArrayList<>(professor1.getStudentMap().values());
        double averageScore1 = ScoreCalculator.calculateScore(studentPool1);
        List<Student> studentPool2 = new ArrayList<>(professor2.getStudentMap().values());
        double averageScore2 = ScoreCalculator.calculateScore(studentPool2);

        System.out.println("\n");
        System.out.println("The average score for the students assigned to the professor1 is: " + averageScore1);
        System.out.println("The average score for the students assigned to the professor2 is: " + averageScore2);

        // database

        final String url = "jdbc:sqlite:src\\data\\database.db";
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE students ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "column1 TEXT,"
                    + "column2 INTEGER"
                    + ")";

//            statement.execute(createTableSQL);
            statement.close();
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
