package hw5;

import java.io.Serializable;


public class TestOpNew implements IComputationTask<String>, Serializable{


    @Override
    public String executeTask() {
        return "newTask";
    }
}
