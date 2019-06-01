package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.impl.Bgs.CommandEncDec;

import java.util.List;

public class FollowUserListAck extends Ack {
    //fields
    private short numOfUsers;
    private List<String> userList;

    public FollowUserListAck(short commandOpcode , short numOfUsers , List<String> userList) {
        super(commandOpcode);
        this.numOfUsers = numOfUsers;
        this.userList = userList;
    }

    @Override
    public byte[] getBytes() {
        byte[] base = super.getBytes();
        byte[] usersNum = CommandEncDec.shortToBytes(numOfUsers);
        byte[] usersBytes = buildBytes(userList);
        int counter = 0;
        byte[] output = new byte[base.length+usersNum.length+usersBytes.length];
        counter=updateOutput(base,output,counter);
        counter=updateOutput(usersNum,output,counter);
        updateOutput(usersBytes,output,counter);
        return output;
    }

    /**
     * Gets a List of string and Return an array of bytes representing that list.
     * @param userList
     * @return
     */
    private byte[] buildBytes(List<String> userList) {
        int size = getSize(userList);
        byte[] output = new byte[size];
        int counter = 0;
        for(String s :userList){
            byte[] tmp = s.getBytes();
            counter=updateOutput(tmp,output,counter);
            output[counter] = (byte) '\0';
            counter++;
        }
        return output;
    }

    private int getSize(List<String> userList) {
        int sum =0;
        for(String s:userList){
            sum+=s.length()+1;
        }
        return sum;
    }
}
