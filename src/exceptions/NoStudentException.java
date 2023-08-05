package exceptions;

import java.util.UUID;

public class NoStudentException extends Exception{
    public NoStudentException(UUID id){
        super("There is no student registered with this id: " + id);
    }

    public NoStudentException(){
        super("No student found!");
    }
}
