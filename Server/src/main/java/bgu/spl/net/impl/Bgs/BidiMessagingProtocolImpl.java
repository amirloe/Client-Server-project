package bgu.spl.net.impl.Bgs;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.Commands.*;
import bgu.spl.net.impl.Bgs.Commands.Error;

import java.util.List;
import java.util.Queue;
import java.util.Vector;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Command> {
    //fields
    private boolean shouldTerminate;
    private int connectionId;
    private Connections<Command> connections;
    private SocialDB dataBase;
    private User myUser;

    public BidiMessagingProtocolImpl(SocialDB dataBase) {

        this.dataBase = dataBase;
        myUser = null;
    }

    @Override
    public void start(int connectionId, Connections<Command> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }


    @Override
    public void process(Command message) {
        message.execute(this);

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void login(String userName, String password, short opCode) {
        //check if the user is registered, if the password is correct and if this protocol not logged in already.
        if (!(dataBase.isRegister(userName) &&
                dataBase.checkPassword(userName, password) &&
                myUser == null)) {
            Error error = new Error(opCode);
            connections.send(connectionId, error);
        }
        else {
            //if the log in worked
            if (dataBase.login(userName, connectionId)) {
                //send ack to the client:
                Ack ack = new Ack(opCode);
                connections.send(connectionId, ack);

                myUser = dataBase.findUser(userName);
                //send the pending messages
                Queue<MessagePost> pendingMessages;
                //sync with the send message so we dont miss any message
                synchronized (dataBase.findUser(userName)) {
                    pendingMessages = myUser.getPendingMesseges();
                }
                sendMessages(pendingMessages);
            }
            else {
                Error error = new Error(opCode);
                connections.send(connectionId, error);
            }
        }
    }

    /**
     * send each message from the queue
     */
    private void sendMessages(Queue<MessagePost> pendingMessages) {
        while (!pendingMessages.isEmpty()) {

            MessagePost message = pendingMessages.poll();
            char type = ' ';
            if (message.getType() == MessagePost.PostType.PM)
                type = '0';
            else
                type = '1';

            Notification noti = new Notification(type, message.getUsername(), message.getContant());
            connections.send(connectionId,noti);
        }
    }

    public void follow(Follow.FollowStatus status, List<String> usernameList, short opCode) {
        //if the user is not logged in
        if (myUser == null)
            connections.send(connectionId, new Error(opCode));
        else {
            List<String> output = new Vector<>();
            boolean success = false;
            for (String userName : usernameList) {
                //follow the users in the list
                if (status == Follow.FollowStatus.FOLLOW) {
                    String followedUser;
                    if ((followedUser = dataBase.tryToFollow(myUser, userName)) != null) {
                        output.add(followedUser);
                        success = true;
                    }
                }
                //un-follow the users in the list
                else if (status == Follow.FollowStatus.UNFOLLOW) {
                    String test;
                    if ((test = dataBase.tryToUnfollow(myUser, userName)) != null) {
                        output.add(test);
                        success = true;
                    }
                }
            }
            if (success)//if the un/follow worked
                connections.send(connectionId, new FollowUserListAck(opCode, (short) output.size(), output));
            else
                connections.send(connectionId, new Error(opCode));
        }
    }

    public void privateMessage(String userName, String contant, short opCode) {
        //mannage errors,
        if (myUser == null || dataBase.findUser(userName) == null)
            connections.send(connectionId, new Error(opCode));
        else {
            MessagePost mp = new PrivateMessage(myUser.getName(), contant, userName);
            //add the message to the posts data
            dataBase.post(myUser.getName(), mp);
            //if the user is logged in send him the message
            message(mp, userName, '0');
            //"complete"
            connections.send(connectionId, new Ack(opCode));
        }

    }

    public void stat(String userName, short opCode) {
        if (myUser == null || dataBase.findUser(userName) == null)
            connections.send(connectionId, new Error(opCode));
        else {
            User statUser = dataBase.findUser(userName);
            Ack ack = new StatAck(opCode,
                    (short) statUser.getNumOfPosts(),
                    (short) statUser.getNumOfFollowers(),
                    (short) statUser.getNumOfFollowing());
            connections.send(connectionId, ack);
        }
    }

    public void register(String userName, String password, short opCode) {
        //two users are trying to register at the same time

                if (dataBase.register(userName, password)) {
                    Ack ack = new Ack(opCode);
                    connections.send(connectionId, ack);
                } else {
                    Error error = new Error(opCode);
                    connections.send(connectionId, error);
                }

    }

    public void logout(short opCode) {
        if (myUser == null) {
            Error error = new Error(opCode);
            connections.send(connectionId, error);

        } else {
            dataBase.removeFromLoggedIn(myUser);//sync inside the socialDB
            myUser = null;
            Ack ack = new Ack(opCode);
            connections.send(connectionId, ack);
            shouldTerminate = true;
        }

    }

    public void userList(short opCode) {
        if (myUser == null) {//not logged in
            Error error = new Error(opCode);
            connections.send(connectionId, error);

        } else {
            List<String> registeredUsers = dataBase.getRegisteredUsersName();
            short size = (short) registeredUsers.size();
            Ack ack = new FollowUserListAck(opCode, size, registeredUsers);
            connections.send(connectionId, ack);


        }
    }

    public void post(short opCode, String body, List<String> taggedUsers) {
        if (myUser == null) {//not logged in
            Error error = new Error(opCode);
            connections.send(connectionId, error);

        } else {
            MessagePost mp = new PublicPost(myUser.getName(), body);
            dataBase.post(myUser.getName(), mp);
            List<String> whoFollowMe = myUser.getFollower();// this methos should return a new instance of that list, not the same pointer
            for (String name : taggedUsers) {
                if (!whoFollowMe.contains(name) && dataBase.isRegister(name))
                    whoFollowMe.add(name);
            }
            for (String name : whoFollowMe) {
                message(mp, name, '1');
            }
            Ack ack = new Ack(opCode);
            connections.send(connectionId, ack);
        }


    }

    /**
     * The function send a message to a user depend on its logged in state
     * @param mp the message
     * @param userName the user
     * @param type '0' for pm '1' for public
     */
    private void message(MessagePost mp, String userName, char type) {
        //sync with the logout
        synchronized (dataBase.findUser(userName)) {
            if (dataBase.isLoggedin(userName)) {
                int otherUserConnection = dataBase.getConnectionid(userName);
                connections.send(otherUserConnection, new Notification(type, myUser.getName(), mp.getContant()));
            }
            //put in the recieved user queue the message
            else {
                dataBase.findUser(userName).sendPost(mp);
            }
        }


    }

}

