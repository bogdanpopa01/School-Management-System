package utils;

import classes.Person;
import classes.Professor;
import classes.Student;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonManager {
    public static void saveJson(String dir, List<Person> personList) {
        var jsonArray = new JSONArray();
        for (var p : personList) {
            if (p instanceof Student) {
                Student student = (Student) p;
                saveStudentJson(student, jsonArray);
            } else if (p instanceof Professor) {
                Professor professor = (Professor) p;
                saveProfessorJson(professor, jsonArray);
            } else {
                throw new IllegalArgumentException("Unsupported person type: " + p.getClass().getName());
            }
        }

        try (var file = new FileWriter(dir)) {
            jsonArray.write(file, 3, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Person> readStudentsJson(String dir) {
        List<Person> result = new ArrayList<>();
        try (var file = new FileInputStream(dir)) {
            JSONTokener tokener = new JSONTokener(file);
            JSONArray jsonArray = new JSONArray(tokener);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Student student = readStudentJson(jsonObject);
                result.add(student);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<Person> readProfessorsJson(String dir) {
        List<Person> result = new ArrayList<>();
        try (var file = new FileInputStream(dir)) {
            JSONTokener tokener = new JSONTokener(file);
            JSONArray jsonArray = new JSONArray(tokener);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UUID id = UUID.fromString(jsonObject.getString("professorIdd"));
                String name = jsonObject.getString("professorName");
                Date dateOfBirth = DateConverter.stringToDate(jsonObject.getString("dateOfBirthProfessor"));
                String taughtCourse = jsonObject.getString("taughtCourse");
                JSONArray studentsArray = jsonObject.getJSONArray("students");
                Map<UUID, Student> studentMap = new HashMap<>();
                for (int j = 0; j < studentsArray.length(); j++) {
                    JSONObject jsonStudent = studentsArray.getJSONObject(j);
                    Student student = readStudentJson(jsonStudent);
                    studentMap.put(student.getId(),student);
                }
                Professor professor = new Professor(id,name,dateOfBirth,taughtCourse);
                professor.setStudentMap(studentMap);
                result.add(professor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static Student readStudentJson(JSONObject jsonObject) {
        UUID id = UUID.fromString(jsonObject.getString("studentId"));
        String name = jsonObject.getString("studentName");
        Date dateOfBirth = DateConverter.stringToDate(jsonObject.getString("dateOfBirthStudent"));
        UUID professorId = UUID.fromString(jsonObject.getString("professorId"));
        JSONArray gradesArray = jsonObject.getJSONArray("grades");
        int[] grades = new int[gradesArray.length()];
        for (int j = 0; j < grades.length; j++) {
            grades[j] = gradesArray.getInt(j);
        }
        return new Student(id, name, dateOfBirth, grades, professorId);
    }

    private static void saveStudentJson(Student student, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("studentId", student.getId().toString());
        jsonObject.put("studentName", student.getName());
        jsonObject.put("dateOfBirthStudent", DateConverter.dateToString(student.getDateOfBirth()));
        jsonObject.put("professorId", student.getProfessorId().toString());
        JSONArray gradesArray = new JSONArray();
        for (var grade : student.getGrades()) {
            gradesArray.put(grade);
        }
        jsonObject.put("grades", gradesArray);
        jsonArray.put(jsonObject);
    }

    private static void saveProfessorJson(Professor professor, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("professorIdd", professor.getId().toString());
        jsonObject.put("professorName", professor.getName());
        jsonObject.put("dateOfBirthProfessor", DateConverter.dateToString(professor.getDateOfBirth()));
        jsonObject.put("taughtCourse", professor.getTaughtCourse());
        JSONArray studentsArray = new JSONArray();
        for (Map.Entry<UUID, Student> entry : professor.getStudentMap().entrySet()) {
            Student student = entry.getValue();

            saveStudentJson(student, studentsArray);
        }
        jsonObject.put("students", studentsArray);
        jsonArray.put(jsonObject);
    }
}
