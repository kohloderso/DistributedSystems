package hw5;

import java.io.Serializable;


public class TestOp implements IComputationTask<Integer>, Serializable{


    @Override
    public Integer executeTask() {
        return 42;
    }
}
