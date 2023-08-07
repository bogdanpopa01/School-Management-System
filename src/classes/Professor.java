package classes;

import exceptions.NoStudentException;
import interfaces.IWage;

import java.util.*;

public class Professor extends Person implements IWage {
    private String taughtCourse;
    private Map<UUID, Student> studentMap = new HashMap<>();

    public Professor(UUID id, String name, Date date, String taughtCourse) {
        super(id, name, date);
        this.taughtCourse = taughtCourse;
    }

    public void addStudent(Student s) {
        if (!studentMap.containsKey(s.getId())) {
            studentMap.put(s.getId(), s);
        }
    }

    public Student getStudent(UUID id) throws NoStudentException {
        Student student = studentMap.get(id);
        if (student == null) {
            throw new NoStudentException(id);
        }
        return student;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Professor{id=").append(this.getId()).append(", name=").append(this.getName())
                .append(", dateOfBirth=").append(this.getDateOfBirth()).append(", taughtCourse=").append(this.getTaughtCourse())
                .append(", students=").append(this.getStudentNo() > 0 ? this.getStudentNo() : "no students").append("}");
        return stringBuilder.toString();
    }

    @Override
    public void wage() {
        System.out.println("I earn more than $2000.");
    }

    @Override
    public void workToDo() {
        System.out.println("I have to go to school to teach.");
    }

    public Map<UUID, Student> getStudentMap() {
        return studentMap;
    }

    public int getStudentNo() {
        return studentMap.size();
    }

    public String getTaughtCourse() {
        return taughtCourse;
    }

    public void setTaughtCourse(String taughtCourse) {
        this.taughtCourse = taughtCourse;
    }

    public void setStudentMap(Map<UUID, Student> studentMap) {
        this.studentMap = studentMap;
    }
}

