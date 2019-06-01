package bgu.spl.net.impl.Bgs;
public class MessagePost {

    public enum PostType{
        PM,PUBLIC;
    }
    //Fields;
    private PostType type;
    private String username;
    private String Contant;

    public MessagePost(PostType type, String username, String contant) {
        this.type = type;
        this.username = username;
        Contant = contant;
    }

    /**
     * getters:
     */
    public PostType getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getContant() {
        return Contant;
    }
}
