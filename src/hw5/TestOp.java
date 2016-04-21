package hw5;

import java.io.Serializable;

/**
 * Created by christina on 21.04.16.
 */
public class TestOp implements IComputationTask<Integer>, Serializable{


    @Override
    public Integer executeTask() {
        return 42;
    }
}
