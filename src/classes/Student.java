package classes;

import interfaces.IPassed;
import interfaces.IWage;
import interfaces.IWorkToDo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class Student extends Person implements IWage, IPassed, Cloneable {
    private int[] grades;

    public Student(UUID id, String name, Date date, int[] grades) {
        super(id, name, date);
        this.grades = grades;
    }

    @Override
    public void workToDo() {
        System.out.println("I have to go to school to learn stuff.");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Student{id=").append(this.getId()).append(", name=").append(this.getName())
                .append(", dateOfBirth=").append(this.getDateOfBirth()).append(", grades=").append(Arrays.toString(this.getGrades())).append("}");
        return stringBuilder.toString();
    }

    @Override
    protected Student clone() throws CloneNotSupportedException {
        Student clonedStudent = (Student) super.clone();
        clonedStudent.grades = new int[this.getGrades().length];
        for(int i=0;i<this.getGrades().length;i++){
            clonedStudent.grades[i] = this.grades[i];
        }
        return clonedStudent;
    }

    public int[] getGrades() {
        return grades;
    }

    public int getGrade(int index){
        return this.grades[index];
    }

    public void setGrades(int[] grades) {
        this.grades = grades;
    }

}
