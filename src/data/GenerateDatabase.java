package data;

import utils.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GenerateDatabase {
    public static void generateDatabase(){
        try(Connection connection = DriverManager.getConnection(Constants.databaseUrl)) {
            System.out.println("\n");
            System.out.println("Database created successfully!");
            Statement statement  = connection.createStatement();

            String createProfessorsTable = "CREATE TABLE IF NOT EXISTS professors ("
                    + "id TEXT PRIMARY KEY,"
                    + "name TEXT,"
                    + "birth_date TEXT,"
                    + "taught_course TEXT"
                    + ")";

            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students ("
                    + "id TEXT PRIMARY KEY,"
                    + "name TEXT,"
                    + "birth_date TEXT,"
                    + "professor_id TEXT,"
                    + "FOREIGN KEY(professor_id) REFERENCES professors(id)"
                    + ")";

            String createGradesTable = "CREATE TABLE IF NOT EXISTS grades ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "value INTEGER,"
                    + "student_id TEXT,"
                    + "FOREIGN KEY(student_id) REFERENCES students(id)"
                    + ")";

            statement.execute(createProfessorsTable);
            statement.execute(createStudentsTable);
            statement.execute(createGradesTable);

            System.out.println("Professors table created successfully!");
            System.out.println("Students table created successfully!");
            System.out.println("Grades table created successfully!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
