package hw5;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by christina on 25.04.16.
 */
public interface Listener extends Remote {
    public void onThinkingCompleted(String answer) throws RemoteException;
}
