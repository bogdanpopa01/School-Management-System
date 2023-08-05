package interfaces;

public interface IWage {
    default void wage(){
        System.out.println("I only have a scholarship.");
    }
}
