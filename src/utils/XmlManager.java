package utils;

import classes.Person;
import classes.Professor;
import classes.Student;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class XmlManager {

    public static void saveXml(String dir, List<Person> personList) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var document = builder.newDocument();

        Element root;

        boolean hasProfessors = personList.stream().anyMatch(p -> p instanceof Professor);

        if (hasProfessors) {
            root = document.createElement("professors");
        } else {
            root = document.createElement("students");
        }

        document.appendChild(root);

        for (var p : personList) {
            if (p instanceof Student) {
                Student student = (Student) p;
                saveStudentXml(student, document, root);
            } else if (p instanceof Professor) {
                Professor professor = (Professor) p;
                saveProfessorXml(professor, document, root);
            } else {
                throw new IllegalArgumentException("Unsupported person type: " + p.getClass().getName());
            }
        }

        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        try (var file = new FileOutputStream(dir)) {
            transformer.transform(new DOMSource(document), new StreamResult(file));
        }
    }

    public static List<Person> readStudentsXml(String dir) throws Exception {
        List<Person> result = new ArrayList<>();

        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var document = builder.parse(dir);
        var root = document.getDocumentElement();

        var studentsNodes = root.getElementsByTagName("student");
        for (int i = 0; i < studentsNodes.getLength(); i++) {
            Student student = readStudentXml(studentsNodes, i);
            result.add(student);
        }
        return result;
    }

    public static List<Person> readProfessorsXml(String dir) throws Exception {
        List<Person> result = new ArrayList<>();

        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var document = builder.parse(dir);
        var root = document.getDocumentElement();

        var professorsNode = root.getElementsByTagName("professor");
        for (int i = 0; i < professorsNode.getLength(); i++) {
            Professor professor = readProfessorXml(professorsNode, i);
            result.add(professor);
        }

        return result;
    }

    private static Professor readProfessorXml(NodeList professorsNode, int index) {
        Element professorNode = (Element) professorsNode.item(index);
        UUID professorId = UUID.fromString(professorNode.getElementsByTagName("professorId").item(0).getTextContent());
        String professorName = professorNode.getElementsByTagName("professorName").item(0).getTextContent();
        Date dateOfBirthProfessor = DateConverter.stringToDate(professorNode.getElementsByTagName("dateOfBirthProfessor").item(0).getTextContent());
        String taughtCourse = professorNode.getElementsByTagName("taughtCourse").item(0).getTextContent();

        NodeList studentsNode = professorNode.getElementsByTagName("student");
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < studentsNode.getLength(); i++) {
            Student student = readStudentXml(studentsNode, i);
            studentList.add(student);
        }
        Professor professor = new Professor(professorId, professorName, dateOfBirthProfessor, taughtCourse);
        Map<UUID, Student> studentMap = new HashMap<>();
        for (var student : studentList) {
            studentMap.put(student.getId(), student);
        }
        professor.setStudentMap(studentMap);
        return professor;
    }


    private static Student readStudentXml(NodeList studentsNodes, int index) {
        Element studentNode = (Element) studentsNodes.item(index);

        UUID id = UUID.fromString(studentNode.getElementsByTagName("studentId").item(0).getTextContent());
        String name = studentNode.getElementsByTagName("studentName").item(0).getTextContent();
        Date date = DateConverter.stringToDate(studentNode.getElementsByTagName("dateOfBirthStudent").item(0).getTextContent());
        UUID professorId = UUID.fromString(studentNode.getElementsByTagName("studentProfessorId").item(0).getTextContent());

        NodeList gradeNodes = studentNode.getElementsByTagName("grade");
        int[] grades = new int[gradeNodes.getLength()];
        for (int i = 0; i < gradeNodes.getLength(); i++) {
            Element gradeNode = (Element) gradeNodes.item(i);
            int gradeValue = Integer.parseInt(gradeNode.getAttribute("value"));
            grades[i] = gradeValue;
        }

        return new Student(id, name, date, grades, professorId);
    }


    private static void saveStudentXml(Student student, Document document, Element root) {
        var studentNode = document.createElement("student");

        var studentIdNode = document.createElement("studentId");
        studentIdNode.setTextContent(student.getId().toString());
        studentNode.appendChild(studentIdNode);

        var studentNameNode = document.createElement("studentName");
        studentNameNode.setTextContent(student.getName());
        studentNode.appendChild(studentNameNode);

        var studentDateOfBirthNode = document.createElement("dateOfBirthStudent");
        studentDateOfBirthNode.setTextContent(DateConverter.dateToString(student.getDateOfBirth()));
        studentNode.appendChild(studentDateOfBirthNode);

        var studentProfessorIdNode = document.createElement("studentProfessorId");
        studentProfessorIdNode.setTextContent(student.getProfessorId().toString());
        studentNode.appendChild(studentProfessorIdNode);

        var studentGradesNode = document.createElement("grades");
        for (var grade : student.getGrades()) {
            var studentGradeNode = document.createElement("grade");
            studentGradeNode.setAttribute("value", Integer.toString(grade));
            studentGradesNode.appendChild(studentGradeNode);
        }
        studentNode.appendChild(studentGradesNode);
        root.appendChild(studentNode);
    }

    private static void saveProfessorXml(Professor professor, Document document, Element root) {
        var professorNode = document.createElement("professor");

        var professorIdNode = document.createElement("professorId");
        professorIdNode.setTextContent(professor.getId().toString());
        professorNode.appendChild(professorIdNode);

        var professorNameNode = document.createElement("professorName");
        professorNameNode.setTextContent(professor.getName());
        professorNode.appendChild(professorNameNode);

        var professorDateOfBirthNode = document.createElement("dateOfBirthProfessor");
        professorDateOfBirthNode.setTextContent(DateConverter.dateToString(professor.getDateOfBirth()));
        professorNode.appendChild(professorDateOfBirthNode);

        var professorTaughtCourseNode = document.createElement("taughtCourse");
        professorTaughtCourseNode.setTextContent(professor.getTaughtCourse());
        professorNode.appendChild(professorTaughtCourseNode);

        var studentsNode = document.createElement("students");
        professorNode.appendChild(studentsNode);

        ArrayList<Student> arrayListStudents = new ArrayList<>(professor.getStudentMap().values());
        for (Student arrayListStudent : arrayListStudents) {
            saveStudentXml(arrayListStudent, document, studentsNode);
        }
        root.appendChild(professorNode);
    }
}
