package utils;

import classes.Person;
import classes.Professor;
import classes.Student;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.util.ArrayList;
import java.util.List;

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
                saveProfessorXml(professor,document,root);
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
