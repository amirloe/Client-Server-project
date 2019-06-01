#ifndef READ_FROM_SERVER__
#define READ_FROM_SERVER__

#include "connectionHandler.h"
class ReadFromServer{
private:
    ConnectionHandler& connectionHandler;

    int* flag;

    short bytesToShort(char *bytesArr);

    std::string getCommand(short opcode);

    bool isSpecialCommand(short command);

public:
    ReadFromServer(ConnectionHandler &connectionHandler, int *flag);

    void run();
};

#endif