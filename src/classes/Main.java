package classes;

import data.DatabaseQuery;
import data.GenerateDatabase;
import data.PopulateDatabase;
import exceptions.NoStudentException;
import interfaces.IPassed;
import utils.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Person person = new Student(UUID.randomUUID(), "Gigi", new Date(), gradesPool1, UUID.randomUUID());
        person.workToDo();

        // child member
        // demonstration of an interface, with static and default methods
        Student student1 = new Student(UUID.randomUUID(), "Florian", DateConverter.stringToDate("2002-01-30"), gradesPool1, UUID.randomUUID());
        Student student2 = new Student(UUID.randomUUID(), "Dorin", DateConverter.stringToDate("2003-01-30"), gradesPool2, UUID.randomUUID());
        Student student3 = new Student(UUID.randomUUID(), "Catalin", DateConverter.stringToDate("2004-01-30"), gradesPool3, UUID.randomUUID());

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
        Professor professor1 = new Professor(UUID.randomUUID(), "Mihail", DateConverter.stringToDate("1999-10-20"), "PAW");

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
            FileManager.saveTxt(Constants.studentsTxt, orderedStudents);
            System.out.println("\nstudents.txt generated!\n");
            FileManager.saveTxt(Constants.professorsTxt, professorList);
            System.out.println("professors.txt generated!\n");
        };

        Runnable saveBinaryRunnable = () -> {
            FileManager.saveBinary(Constants.studentsBinary, orderedStudents);
            System.out.println("students.dat generated!\n");
            FileManager.saveBinary(Constants.professorsBinary, professorList);
            System.out.println("professors.dat generated!\n");
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(saveTxtRunnable);
        executorService.execute(saveBinaryRunnable);

        executorService.shutdown();

        try {
            boolean tasksCompleted = executorService.awaitTermination(3, TimeUnit.SECONDS);
            if (!tasksCompleted) {
                System.out.println("Warning: Some tasks didn't finish within the timeout.");
            }

            List<Person> readStudents = FileManager.readBinaryStudents(Constants.studentsBinary);
            FileManager.saveTxt(Constants.readStudentsTxt, readStudents);
            System.out.println("readStudents.txt generated!\n");

            List<Person> readProfessors = FileManager.readBinaryProfessors(Constants.professorsBinary);
            FileManager.saveTxt(Constants.readProfessorsTxt, readProfessors);
            System.out.println("readProfessors.txt generated!\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // assigning students to professors using threads
        professor1.getStudentMap().clear();
        professor2.getStudentMap().clear();

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

        // database creation and population
        GenerateDatabase.generateDatabase();
        List<Professor> professors = new ArrayList<>();

        professors.add(professor1);
        professors.add(professor2);

        List<Student> allStudents = Stream.concat(studentPool1.stream(), studentPool2.stream())
                .distinct()
                .collect(Collectors.toList());

        PopulateDatabase.addProfessors(professors);
        PopulateDatabase.addStudents(allStudents);
        PopulateDatabase.addGrade(allStudents);

        //  or like this:      PopulateDatabase.populateDatabase(professors,allStudents);

        // database query
        System.out.println("\nThe query results:\n");
        DatabaseQuery.databaseQuery();

        // json format
        JsonManager.saveJson(Constants.studentsJson,professor1.getStudentMap().values().stream().collect(Collectors.toList()));
        System.out.println("students.json generated!\n");
        JsonManager.saveJson(Constants.professorsJson,professorList);
        System.out.println("professors.json generated!\n");

        List<Person> readStudentsJson = JsonManager.readStudentsJson(Constants.studentsJson);
        JsonManager.saveJson(Constants.readStudentsJson,readStudentsJson);
        System.out.println("readStudents.json generated!\n");
        List<Person> readProfessorsJson = JsonManager.readProfessorsJson(Constants.professorsJson);
        JsonManager.saveJson(Constants.readProfessorsJson,readProfessorsJson);
        System.out.println("readProfessors.json generated!\n");

        // xml format
        try {
            XmlManager.saveXml(Constants.studentsXml,professor2.getStudentMap().values().stream().collect(Collectors.toList()));
            System.out.println("students.xml generated!\n");
            XmlManager.saveXml(Constants.professorsXml,professorList);
            System.out.println("professors.xml generated!\n");

            List<Person> readStudentsXml = XmlManager.readStudentsXml(Constants.studentsXml);
            XmlManager.saveXml(Constants.readStudentsXml,readStudentsXml);
            System.out.println("readStudents.xml generated!\n");
            List<Person> readProfessorsXml = XmlManager.readProfessorsXml(Constants.professorsXml);
            XmlManager.saveXml(Constants.readProfessorsXml,readProfessorsXml);
            System.out.println("readProfessors.xml generated!\n");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
