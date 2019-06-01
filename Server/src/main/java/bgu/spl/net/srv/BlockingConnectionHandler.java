package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, bgu.spl.net.srv.bidi.ConnectionHandler<T> {

    private final int id;
    private Connections<T> connections;
    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(int id,Connections<T> connections,Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        this.id=id;
        this.connections=connections;
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        protocol.start(id,connections);
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }
            close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
        connections.disconnect(id);
    }

    @Override
    public void send(T msg) {
        if (msg != null) {
            try {
                byte[] bytes = encdec.encode(msg);
                synchronized (this) {
                    out.write(bytes);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}



