package bgu.spl.net.impl.echo;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.CommandEncDec;
import bgu.spl.net.impl.Bgs.SocialDB;
import bgu.spl.net.srv.Server;

public class EchoServer {
    public static void main(String[] args) {
        System.out.println(EchoServer.class.getName());
        SocialDB data = new SocialDB();
       Server s = Server.threadPerClient(7778,
               ()->new BidiMessagingProtocolImpl(data),
               ()->new CommandEncDec());
/*
    Server s = Server.reactor(7,
            7778,
            ()->new BidiMessagingProtocolImpl(data),
            ()->new CommandEncDec());
*/
       s.serve();
    }
}
