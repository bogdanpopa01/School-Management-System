package classes;

import exceptions.NoStudentException;
import interfaces.IPassed;
import utils.DateConverter;
import utils.FileManager;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
        Person person = new Student(UUID.randomUUID(),"Gigi",new Date(),gradesPool1);
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


        // another child member
        Professor professor1 = new Professor(UUID.randomUUID(), "Mihail", DateConverter.stringToDate("1956-10-20"), "PAW");

        System.out.println("\nI am a professor:");
        System.out.println(professor1);
        professor1.wage();
        professor1.workToDo();

        professor1.addStudent(student1);
        professor1.addStudent(student2);
        professor1.addStudent(student3);

        // file managing
        Professor professor2 = new Professor(UUID.randomUUID(), "Yani", DateConverter.stringToDate("1956-01-19"), "SGBD");

        professor2.addStudent(student1);
        professor2.addStudent(student2);

        List<Person> orderedStudents = new ArrayList<>();
        for (Map.Entry<UUID, Student> entry : professor1.getStudentMap().entrySet()) {
            orderedStudents.add(entry.getValue());
        }
        Collections.sort(orderedStudents, new Comparator<Person>() {
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

        FileManager.saveTxt("src\\files\\students.txt", orderedStudents);
        FileManager.saveBinary("src\\files\\students.dat", orderedStudents);
        List<Person> readStudents = FileManager.readBinaryStudents("src\\files\\students.dat");
        FileManager.saveTxt("src\\files\\readStudents.txt", readStudents);

        FileManager.saveTxt("src\\files\\professors.txt", professorList);
        FileManager.saveBinary("src\\files\\professors.dat", professorList);
        List<Person> readProfessors = FileManager.readBinaryProfessors("src\\files\\professors.dat");
        FileManager.saveTxt("src\\files\\readProfessors.txt", readProfessors);
    }


}
