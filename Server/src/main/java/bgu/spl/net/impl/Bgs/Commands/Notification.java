package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.CommandEncDec;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.util.Arrays;

public class Notification extends Command {
    //fields
    private String postingUser;
    private String content;
    private byte typeOfNoty;


    public Notification(char c, String postingUser, String content) {
        super((short) 9);
        if(c=='0')
            typeOfNoty=0;
        else if(c=='1')
            typeOfNoty =1;
        this.postingUser = postingUser;
        this.content = content;

    }




    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {

    }

    @Override
    public byte[] getBytes() {
        byte[] opcode = CommandEncDec.shortToBytes(opCode);
        byte[] userBytes = postingUser.getBytes();
        byte[] contentByte = content.getBytes();
        int length = opcode.length + userBytes.length + contentByte.length + 3;
        byte[] output = new byte[length];
        Integer counter = new Integer(0);

        counter = updateOutput(opcode,output,counter);

        output[counter] =typeOfNoty;
        counter++;

        counter = updateOutput(userBytes,output,counter);
        output[counter] = (byte) '\0';
        counter++;

        counter = updateOutput(contentByte,output,counter);
        output[counter] = (byte) '\0';
        return output;

    }


}
