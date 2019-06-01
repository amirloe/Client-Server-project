package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.CommandEncDec;
import bgu.spl.net.impl.Bgs.SocialDB;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        SocialDB data = new SocialDB();
        int port = Integer.parseInt(args[0]);
        int numOfT = Integer.parseInt(args[1]);
        Server s = Server.reactor(numOfT,
                port,
                ()->new BidiMessagingProtocolImpl(data),
                ()->new CommandEncDec());

        s.serve();
    }
}
