package bgu.spl.net.impl.Bgs;

public class PrivateMessage extends MessagePost {
    String otherUser;
    public PrivateMessage(String username, String contant,String otherUser) {
        super(PostType.PM, username, contant);
        this.otherUser = otherUser;
    }
}
