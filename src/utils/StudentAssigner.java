package utils;

import classes.Professor;
import classes.Student;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StudentAssigner {
    public static void assignStudentToProfessor(Professor professor, ConcurrentLinkedQueue<Student> unassignedStudents){
        Random random = new Random();
        while(!unassignedStudents.isEmpty()){
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Student student = unassignedStudents.poll();
            if(student!=null){
                professor.addStudent(student);
            }
        }
    }
}
