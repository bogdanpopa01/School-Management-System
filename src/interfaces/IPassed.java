package interfaces;

public interface IPassed {
    static boolean isPassed(int[] grades){
        boolean passed = true;
        for(int g : grades){
            if(g < 5){
                passed = false;
            }
        }
        return passed;
    }
}
