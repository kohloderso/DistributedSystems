package hw6;

import java.io.Serializable;
import java.util.concurrent.Callable;


public class CallableTest implements Callable<String>, Serializable {


    @Override
    public String call() throws Exception {
        Thread.sleep(10000);
        return "Done";
    }
}
