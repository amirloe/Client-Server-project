package bgu.spl.net.srv.bidi;

import bgu.spl.net.api.bidi.Connections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    //fields
    private ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionsMap;

    public ConnectionsImpl(){
        connectionsMap = new ConcurrentHashMap<>();
    }
    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> handler = connectionsMap.get(connectionId);
        if(handler!=null) {
            handler.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer,ConnectionHandler<T>> entry: connectionsMap.entrySet()) {
            entry.getValue().send(msg);
            
        }

    }

    @Override
    public void disconnect(int connectionId) {
        connectionsMap.remove(connectionId);

    }
    public void addConnection(int id,ConnectionHandler<T> handler){
        connectionsMap.putIfAbsent(id,handler);

    }
}
