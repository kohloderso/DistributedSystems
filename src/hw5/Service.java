package hw5;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Service extends Remote {

    public int plus(int x, int y) throws RemoteException;

    public int minus(int x, int y) throws RemoteException;

    public int multiply(int x, int y) throws RemoteException;

    public int lucas(int x) throws RemoteException;

    public int longOp(int x) throws RemoteException;

    public <T>  T computationTask(IComputationTask<T> t) throws RemoteException;

}
