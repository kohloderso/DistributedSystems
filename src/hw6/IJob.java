package hw6;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface IJob<T> extends Remote {

    boolean isDone() throws RemoteException;

    T getResult() throws RemoteException;
}