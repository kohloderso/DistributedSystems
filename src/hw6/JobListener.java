package hw6;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JobListener<T> extends Remote {
    void onJobDone(T result) throws RemoteException;
}
