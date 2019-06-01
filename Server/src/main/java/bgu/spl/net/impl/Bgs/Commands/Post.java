package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

public class Post extends Command {

    //fields
    private String body;
    private List<String> taggedUsers;
    public Post(byte[] bytes, int len, Charset utf8) {
        super((short)5);
        body = new String(bytes,2,len-2,utf8);
        taggedUsers = new Vector<>();
        initUsers();

    }

    private void initUsers() {
        String temp = body;
        String tUser;
        //find the @ in the post and initial the tagged users list
        while (temp.length()>0) {
            int index = temp.indexOf('@');
            if (index != -1) {
                temp = temp.substring(index);
                int indexOfSpace = temp.indexOf(' ');
                if(indexOfSpace!=-1) {
                    tUser = temp.substring(1, indexOfSpace);
                    taggedUsers.add(tUser);
                    temp = temp.substring(indexOfSpace);
                }
                else{
                    tUser=temp.substring(1);
                    taggedUsers.add(tUser);
                    temp=temp.substring(temp.length());
                }
            }
            else{ //no @ found
                return;
            }
        }
    }



    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
        protocol.post(opCode,body,taggedUsers);

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
