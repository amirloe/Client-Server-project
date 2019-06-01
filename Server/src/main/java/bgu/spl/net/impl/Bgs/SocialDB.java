package bgu.spl.net.impl.Bgs;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;

public class SocialDB {
    /**
     * Threadsafe shared resource that holds the social network data
     */
    //Fields
    private Set<User> registerdUser;
    private ConcurrentHashMap<User, Integer> loggedInUsers;
    private ConcurrentLinkedDeque<MessagePost> postsList;

    public SocialDB() {
        registerdUser = new ConcurrentSkipListSet<>();
        loggedInUsers = new ConcurrentHashMap<>();
        postsList = new ConcurrentLinkedDeque<>();
    }

    /**
     * Checks if user is registerd and returns it, if not return null
     */
    public User findUser(String userName) {
        for (User user : registerdUser)
            if (user.isName(userName))
                return user;
        return null;
    }


    /**
     * checks if the user is registerd to the data
     */
    public boolean isRegister(String userName) {
        return (findUser(userName)!=null);
    }

    /**
     * register a user to the database
     */

    public boolean register(String userName, String password) {
        return registerdUser.add(new User(userName, password));
    }

    /**
     * checks if the password is correct
     */
    public boolean checkPassword(String userName, String password) {
        User tmpUser = findUser(userName);
        if(tmpUser!=null)
            return tmpUser.isPassword(password);
        return false;
    }

    /**
     * add user to the logged in map return true if succeed
     */
    public boolean login(String userName, int connectionId) {
        return (loggedInUsers.putIfAbsent(findUser(userName), connectionId)==null);
    }

    /**
     * gets a logged in user name and remove it from the logged in users
     */
    public void removeFromLoggedIn(User myUser) {
        //sync with the send message function
        synchronized (myUser) {
            loggedInUsers.remove(myUser);
        }

    }

    /**
     * Checks if user is logged in by his name
     */
    public boolean isLoggedin(String userName) {
        User tmpUser = findUser(userName);
        if(tmpUser!=null)
            return loggedInUsers.containsKey(tmpUser);
        return false;
    }

    /**
     * try to Follow after user, if success return the name of the user, else return null
     */
    public String tryToFollow(User user, String otherUserName) {
        if(findUser(otherUserName)==null || user.isFollowing(otherUserName))
            return null;
        else{
            user.follow(otherUserName);
            findUser(otherUserName).followedBy(user.getName());
            return otherUserName;
        }
    }
    /**
     * try to Unfollow after user, if success return the name of the user, else return null
     */
    public String tryToUnfollow(User user, String otherUserName) {

        if(findUser(otherUserName)==null || !user.isFollowing(otherUserName))
            return null;
        else{
            user.unFollow(otherUserName);
            findUser(otherUserName).unFollowedBy(user.getName());
            return otherUserName;
        }
    }

    /**
     * return the connection id of a logged in user
     */
    public int getConnectionid(String userName) {
        return loggedInUsers.get(findUser(userName));
    }

    /**
     * adds a post to the postslist and increase the number of posts of a certain user.
     */
    public void post(String name, MessagePost message) {
        findUser(name).newPost();
        postsList.add(message);
    }



    /**
     * return a list of the names of the registered users
     */
    public List<String> getRegisteredUsersName() {
        List<String> toReturn= new Vector<>();

            for (User u: registerdUser) {
                toReturn.add(u.getName());
            }

            return toReturn;


    }
}

