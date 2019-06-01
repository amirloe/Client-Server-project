package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.CommandEncDec;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

public class Follow extends Command {
    public enum FollowStatus {FOLLOW, UNFOLLOW}
    ;
    private FollowStatus status;
    private short numOfUsers;
    private List<String> usernameList;

    public Follow(byte[] bytes, int len, Charset utf8) {
        super((short) 4);
        InitFollowStatus(bytes[2]);
        InitNumOfUsers(bytes);
        InitListOfUsers(bytes, len, utf8);


    }

    private void InitListOfUsers(byte[] bytes, int len, Charset utf8) {
        usernameList = new Vector<>();
        int wordSize = 0;
        for (int i = 5; i <= len; i++) {
            if (bytes[i] == '\0') {
                usernameList.add(new String(bytes, i - wordSize, wordSize, utf8));
                wordSize = 0;
            } else {
                wordSize++;
            }

        }
    }

    private void InitNumOfUsers(byte[] bytes) {
        byte[] numOfUsers = new byte[2];
        numOfUsers[0] = bytes[3];
        numOfUsers[1] = bytes[4];
        this.numOfUsers = CommandEncDec.bytesToShort(numOfUsers);
    }

    private void InitFollowStatus(byte aByte) {
        if (aByte == 0)
            status = FollowStatus.FOLLOW;
        else if (aByte == 1)
            status = FollowStatus.UNFOLLOW;
    }


    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
        protocol.follow(status,usernameList,opCode);
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
