package data;

import classes.Professor;
import classes.Student;
import utils.Constants;
import utils.DateConverter;

import java.sql.*;
import java.util.List;

public class PopulateDatabase {
    public static void addProfessors(List<Professor> professorList){
        try(Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            String deleteProfessor = "DELETE FROM professors";
            PreparedStatement statement = connection.prepareStatement(deleteProfessor);
            statement.execute();
            String insertProfessor = "INSERT INTO professors VALUES (?,?,?,?)";
            statement = connection.prepareStatement(insertProfessor);

            for(var professor : professorList){
                statement.setString(1,professor.getId().toString());
                statement.setString(2,professor.getName());
                statement.setString(3, DateConverter.dateToString(professor.getDateOfBirth()));
                statement.setString(4,professor.getTaughtCourse());

                statement.execute();
            }

            System.out.println("\n");
            System.out.println("Professors table populated!");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addStudents(List<Student> studentList){
        try(Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            String deleteStudents = "DELETE FROM students";
            PreparedStatement statement = connection.prepareStatement(deleteStudents);
            statement.execute();
            String insertStudents = "INSERT INTO students VALUES (?,?,?,?)";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
