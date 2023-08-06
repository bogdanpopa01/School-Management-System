package data;

import classes.Professor;
import classes.Student;
import utils.Constants;
import utils.DateConverter;

import java.sql.*;
import java.util.List;

public class PopulateDatabase {
    public static void populateDatabase(List<Professor> professorList, List<Student> studentList){
        addProfessors(professorList);
        addStudents(studentList);
        addGrade(studentList);
    }

    public static void addProfessors(List<Professor> professorList) {
        try (Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            final String deleteProfessor = "DELETE FROM professors";
            PreparedStatement statement = connection.prepareStatement(deleteProfessor);
            statement.execute();
            final String insertProfessor = "INSERT INTO professors VALUES (?,?,?,?)";
            statement = connection.prepareStatement(insertProfessor);

            for (var professor : professorList) {
                statement.setString(1, professor.getId().toString());
                statement.setString(2, professor.getName());
                statement.setString(3, DateConverter.dateToString(professor.getDateOfBirth()));
                statement.setString(4, professor.getTaughtCourse());

                statement.execute();
            }

            System.out.println("\n");
            System.out.println("Professors table populated!");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addStudents(List<Student> studentList) {
        try (Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            final String deleteStudents = "DELETE FROM students";
            PreparedStatement statement = connection.prepareStatement(deleteStudents);
            statement.execute();
            final String insertStudents = "INSERT INTO students VALUES (?,?,?,?)";
            statement = connection.prepareStatement(insertStudents);

            for (var student : studentList) {
                statement.setString(1, student.getId().toString());
                statement.setString(2, student.getName());
                statement.setString(3, DateConverter.dateToString(student.getDateOfBirth()));
                statement.setString(4, student.getProfessorId().toString());

                statement.execute();
            }

            System.out.println("\n");
            System.out.println("Students table populated!");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addGrade(List<Student> studentList) {
        try (Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            final String deleteGrades = "DELETE FROM grades";
            PreparedStatement statement = connection.prepareStatement(deleteGrades);
            statement.execute();

            final String insertGrade = "INSERT INTO grades (value, student_id) VALUES (?, ?)";
            statement = connection.prepareStatement(insertGrade);

            for (var student : studentList) {
                for (var grade : student.getGrades()) {
                    statement.setInt(1, grade);
                    statement.setString(2, student.getId().toString());
                    statement.execute();
                }
            }

            System.out.println("\n");
            System.out.println("Grades table populated!");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
