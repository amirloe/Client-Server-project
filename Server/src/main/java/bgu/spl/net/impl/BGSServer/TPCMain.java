package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.CommandEncDec;
import bgu.spl.net.impl.Bgs.SocialDB;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        SocialDB data = new SocialDB();
        int port = Integer.parseInt(args[0]);
        Server s = Server.threadPerClient(port,
                ()->new BidiMessagingProtocolImpl(data),
                ()->new CommandEncDec());
        s.serve();
    }
}
