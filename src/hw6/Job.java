package hw6;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;

public class Job<T> extends UnicastRemoteObject implements IJob<T>, JobListener<T>, Serializable {

    private volatile boolean isDone = false; // make it volatile to prevent thread-local caching
    public T result = null;

    public Job() throws RemoteException {
        super();
    }


    @Override
    public boolean isDone() throws RemoteException {
        return isDone;
    }

    @Override
    public T getResult() throws RemoteException {
        return result;
    }

    @Override
    public void onJobDone(T result) throws RemoteException {
        this.result = result;
        isDone = true;

    }
}
