package bgu.spl.net.impl.Bgs;

public class PublicPost extends MessagePost {

    public PublicPost(String username, String contant) {
        super(PostType.PUBLIC, username, contant);
    }
}
