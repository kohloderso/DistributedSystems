package hw5;

import java.io.Serializable;

/**
 * Created by christina on 21.04.16.
 */
public class TestOpNew implements IComputationTask<String>, Serializable{


    @Override
    public String executeTask() {
        return "newTask";
    }
}
