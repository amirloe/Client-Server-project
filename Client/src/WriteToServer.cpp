//
// Created by amirloe@wincs.cs.bgu.ac.il on 12/25/18.
//
#include "../include/connectionHandler.h"
#include "../include/WriteToServer.h"




    void WriteToServer::shortToBytes(short num, char *bytesArr) {
        bytesArr[0] = ((num >> 8) & 0xFF);
        bytesArr[1] = (num & 0xFF);
    }

    short WriteToServer::getOpCode(std::string command) {
        if (command == "REGISTER")
            return 1;
        if (command == "LOGIN")
            return 2;
        if (command == "LOGOUT")
            return 3;
        if (command == "FOLLOW")
            return 4;
        if (command == "POST")
            return 5;
        if (command == "PM")
            return 6;
        if (command == "USERLIST")
            return 7;
        if (command == "STAT")
            return 8;
        return 0;

    }

    int WriteToServer::getNumOfSpaces(short code) {
        switch (code) {
            case (1)://Register
                return 1;
            case (2)://Login
                return 1;
            case (5)://Post
                return 0;
            case (6)://PM
                return 1;
            case (8)://Stat
                return 0;


        }
        return 0;
    }

    //add string to the bytes array
    int WriteToServer::addString(char *bytes, std::string word, int size) {
        const char *b = word.c_str();
        for (unsigned int i = 0; i < word.size(); i++) {
            bytes[size] = b[i];
            size++;
        }
        bytes[size] = '\0';
        size++;
        return size;
    }

    //puts in bytes the encoded array , return the size of the array
    int WriteToServer::encode(std::string &userCommand, char *bytes) {
        int bytesSize;
        short opCode;
        
        int index(userCommand.find(" "));
        if (index == -1)
            opCode = getOpCode(userCommand);
        
        else
            opCode = getOpCode(userCommand.substr(0, index));
        shortToBytes(opCode, bytes);
        
        bytesSize = 2;

        if (index != -1) {//When there are any arguments after the command
            userCommand = userCommand.substr(index + 1);
            if (opCode != 4)//all messages beside follow are the same
            {
                int numOfSpaces = getNumOfSpaces(opCode);

                std::string first("");
                std::string second("");

                if (numOfSpaces > 0) {//two arguments
                    first = userCommand.substr(0, userCommand.find(" "));
                    second = userCommand.substr(userCommand.find(" ") + 1);
                } else {//one argument
                    first = userCommand;
                }

                
                bytesSize = addString(bytes, first, bytesSize);

                if (second != "") {
                    bytesSize = addString(bytes, second, bytesSize);
                }

            } 
            else {//Follow parsing
                char sign = userCommand.substr(0, userCommand.find(" "))[0] -'0';//to get the char not the ascii

                userCommand = userCommand.substr(userCommand.find(' ') + 1);
                short numOfUsers = (short) std::stoi(userCommand.substr(0, userCommand.find(" ")));
                userCommand = userCommand.substr(userCommand.find(' ') + 1);
                char numOfu[2];
                shortToBytes(numOfUsers, numOfu);
                //
                bytes[bytesSize] = sign;
                bytesSize++;
                bytes[bytesSize] = numOfu[0];
                bytesSize++;
                bytes[bytesSize] = numOfu[1];
                bytesSize++;
                while (numOfUsers > 0) {
                    if (numOfUsers == 1) {
                        bytesSize = addString(bytes, userCommand, bytesSize);
                    } else {
                        bytesSize = addString(bytes, userCommand.substr(0, userCommand.find(" ")), bytesSize);
                        userCommand = userCommand.substr(userCommand.find(" ") + 1);
                    }
                    numOfUsers--;
                }

            }

        }
        return bytesSize;

    }


    WriteToServer::WriteToServer(ConnectionHandler &connectionHandler, int *flag) : connectionHandler(connectionHandler),flag(flag) {}


    void WriteToServer::run() {
          //std::string lineTotest;
             while (1) {
            *flag = 0;
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            
            std::string line(buf);

            char bytes[bufsize];
            int size = encode(line, bytes);

            if (!connectionHandler.sendBytes(bytes,size)) {
                break;
            }
            
            if(line == "LOGOUT"){
                while(*flag == 0){}
                if(*flag == 1)
                    break;
        
            }
        }
    }

