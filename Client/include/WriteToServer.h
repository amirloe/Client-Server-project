#ifndef WRITE_TO_SERVER__
#define WRITE_TO_SERVER__

#include "connectionHandler.h"

class WriteToServer {
private:
    ConnectionHandler &connectionHandler;

    int* flag;

    void shortToBytes(short num, char *bytesArr);

    short getOpCode(std::string command);

    int getNumOfSpaces(short code);

    int addString(char *bytes, std::string word, int size);

    int encode(std::string &userCommand, char *bytes);


public:
    WriteToServer(ConnectionHandler &connectionHandler, int *flag);

    void run();
};
#endif