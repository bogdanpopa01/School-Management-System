package interfaces;

public interface IWage {
    default void wage(){
        System.out.println("I don't have a salary yet.");
    }
}
