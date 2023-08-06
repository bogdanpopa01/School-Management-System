package data;

import classes.Professor;
import classes.Student;
import utils.Constants;
import utils.FileManager;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class DatabaseQuery {
    public static void databaseQuery(){
        try(Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            Statement statement = connection.createStatement();

            final String query = "SELECT s.id, s.name, s.birth_date, s.professor_id FROM students s, professors p WHERE s.professor_id = p.id AND p.birth_date < '1999-09-30'";
            try(ResultSet resultSet = statement.executeQuery(query)){
                while (resultSet.next()){
                    System.out.printf("Id: %s, Name: %s, Birth_date: %s, Professor id: %s\n",resultSet.getString(1), resultSet.getString(2),resultSet.getString(3),resultSet.getString(4));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
