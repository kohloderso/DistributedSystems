package hw6;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;


public interface Service extends Remote {

    <T> IJob<T> submit(Callable<T> job) throws RemoteException;

}
