//
// Created by amirloe@wincs.cs.bgu.ac.il on 12/26/18.
//

#include "../include/connectionHandler.h"
#include "../include/ReadFromServer.h"

    short ReadFromServer::bytesToShort(char *bytesArr) {
        short result = (short) ((bytesArr[0] & 0xff) << 8);
        result += (short) (bytesArr[1] & 0xff);
        return result;
    }

    std::string ReadFromServer::getCommand(short opcode) {
        switch (opcode) {
            case (1)://Register
                return "REGISTER";
            case (2)://Login
                return "LOGIN";
            case (3)://logout
                return "LOGOUT";
            case (4)://FOLLOW
                return "FOLLOW";
            case (5)://POST
                return "POST";
            case (6)://PM
                return "PM";
            case (7)://USERLIST
                return "USERLIST";
            case (8)://STAT
                return "STAT";
            case (9)://NOTIFICATION
                return "NOTIFICATION";
            case (10)://ACK
                return "ACK";
            case (11)://ERROR
                return "ERROR";
        }
        return 0;
    }

    bool ReadFromServer::isSpecialCommand(short command) {
        if(command==4||command==7||command==8)
            return true;
        return false;
    }

    ReadFromServer::ReadFromServer(ConnectionHandler &connectionHandler, int *flag) : connectionHandler(connectionHandler),flag(flag){}


    void ReadFromServer::run(){
        while(1) {

            std::string answer;
            char bytes[2];
            if (!connectionHandler.getBytes(bytes,2)) {
                break;
            }
            short opcode = bytesToShort(bytes);
            std::string result = getCommand(opcode);

            if(result == "NOTIFICATION"){
                char c;
                connectionHandler.getBytes(&c,1);
                std::string type;
                if(c==0)
                    type="PM";
                else if (c== 1)
                    type = "Public";
                std::string username;
                connectionHandler.getFrameAscii(username,'\0');
                username = username.substr(0, username.size()-1);
                std::string content;
                connectionHandler.getFrameAscii(content,'\0');
                content = content.substr(0, content.size()-1);
                answer = result +" "+type+" "+username+" "+content;
            }
            //error or ack
            else {
                //get the 2 bytes of the response short
                connectionHandler.getBytes(bytes, 2);
                short responseCommand = bytesToShort(bytes);

                std::string ansCommand = getCommand(responseCommand);
                
                if (result == "ERROR") {
                    //notify the busy wait
                    if(ansCommand == "LOGOUT")
                        *flag = -1;
                    
                    answer = result + " " + std::to_string(responseCommand);
                }
                
                else if (result == "ACK") {
                    
                    answer = result + " " + std::to_string(responseCommand);
                    
                    if (isSpecialCommand(responseCommand)) {
                        
                        if (responseCommand == 8) {//stat
                            connectionHandler.getBytes(bytes, 2);
                            short numPosts = bytesToShort(bytes);
                            connectionHandler.getBytes(bytes, 2);
                            short numFollowers = bytesToShort(bytes);
                            connectionHandler.getBytes(bytes, 2);
                            short numFollowing = bytesToShort(bytes);
                            answer += " " + std::to_string(numPosts) +
                                      " " + std::to_string(numFollowers) +
                                      " " + std::to_string(numFollowing);

                        } else {//Follow/userlist
                            
                            connectionHandler.getBytes(bytes, 2);
                            short numOfUsers = bytesToShort(bytes);
                            answer += " " + std::to_string(numOfUsers);
                            while (numOfUsers > 0) {
                                std::string username;
                                connectionHandler.getFrameAscii(username, '\0');
                                username = username.substr(0, username.size() - 1);
                                answer += " " + username;
                                numOfUsers--;
                            }
                        }
                    }
                }
            }

            std::cout <<  answer <<  std::endl;
            if (answer == "ACK 3") {
                
                *flag = 1;
                
                break;
            }
        }

    }

