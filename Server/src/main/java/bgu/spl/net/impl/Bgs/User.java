package bgu.spl.net.impl.Bgs;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User implements Comparable<User> {
    /**
     * A class representing a user and his information:
     */
    //fields
    private String username;
    private String password;
    private Vector<String> followers;
    private Queue<String> following;
    private Queue<MessagePost> pendingMesseges;
    private int numOfPosts;
    //added by tal just an idea
    private ReadWriteLock readWriteLockFollowers = new ReentrantReadWriteLock();

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.followers = new Vector<>();
        this.following = new ConcurrentLinkedDeque<>();
        this.pendingMesseges = new ConcurrentLinkedDeque<>();
        this.numOfPosts =0;
    }

    /**
     *Most of the methods in this class are quarries for certain information of a specific user
     */
    //compares of the name and password:
    public boolean isName(String name){
        return username.equals(name);
    }

    public boolean isPassword(String tPass){
        return password.equals(tPass);
    }
    //checks if the user is following other user
    public boolean isFollowing(String otherUserName) {
        return following.contains(otherUserName);
    }

    //add and remove from following list:
    public void follow(String otherUserName) {
        following.add(otherUserName);
    }

    public void unFollow(String otherUserName) {
        following.remove(otherUserName);
    }
    //name getter
    public String getName() {
        return username;
    }
    //add and remove from followedby list

    public void followedBy(String name) {
        readWriteLockFollowers.writeLock().lock();
        followers.add(name);
        readWriteLockFollowers.writeLock().unlock();
    }



    public void unFollowedBy(String name) {
        readWriteLockFollowers.writeLock().lock();
        followers.remove(name);
        readWriteLockFollowers.writeLock().unlock();
    }

    //add post to the pending messages
    public void sendPost(MessagePost message) {
        pendingMesseges.add(message);

    }

    public void newPost() {
        numOfPosts++;
    }

    //getters for use of the stat function:
    public int getNumOfPosts() {
        return numOfPosts;
    }
    public int getNumOfFollowers() {
        return followers.size();
    }
    public int getNumOfFollowing() {
        return following.size();
    }

    public Queue<MessagePost> getPendingMesseges() {
        return pendingMesseges;
    }


    /**
     * return a list of userNames of the followers
     * @return
     */
    public List<String> getFollower() {
        readWriteLockFollowers.readLock().lock();
        List<String> toReturn = new Vector<>(followers);
        readWriteLockFollowers.readLock().unlock();
        return toReturn;


        }



    @Override
    public int compareTo(User o) {
        return this.getName().compareTo(o.username);
    }


    //readWrite impl for the followers list ,maby not good

}
