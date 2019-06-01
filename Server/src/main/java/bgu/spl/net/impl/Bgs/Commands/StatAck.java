package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.impl.Bgs.CommandEncDec;

public class StatAck extends Ack {
    //fields
    private short numOfPosts;
    private short numOfFollowers;
    private short numOfFollowing;

    public StatAck(short commandOpcode , short numOfPosts,short numOfFollowers,short numOfFollowing) {
        super(commandOpcode);
        this.numOfPosts = numOfPosts;
        this.numOfFollowers=numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    @Override
    public byte[] getBytes() {
        byte[] base = super.getBytes();
        byte[] postsBytes = CommandEncDec.shortToBytes(numOfPosts);
        byte[] followersBytes = CommandEncDec.shortToBytes(numOfFollowers);
        byte[] followingBytes = CommandEncDec.shortToBytes(numOfFollowing);
        int counter =0;
        byte[] output = new byte[base.length+postsBytes.length+followersBytes.length+followingBytes.length];
        counter=updateOutput(base,output,counter);
        counter=updateOutput(postsBytes,output,counter);
        counter=updateOutput(followersBytes,output,counter);
        counter=updateOutput(followingBytes,output,counter);
        return output;
    }
}
