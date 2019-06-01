#include <stdlib.h>
#include <thread>
#include "../include/connectionHandler.h"
#include "../include/WriteToServer.h"
#include "../include/ReadFromServer.h"
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/





int main (int argc, char *argv[]) {
    if (argc < 3) {
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        return 1;
    }
	
	//From here we will see the rest of the ehco client implementation:
    int * flag = new int(0);
    WriteToServer write(connectionHandler, flag);
    std::thread th2 (&WriteToServer::run, &write);
    ReadFromServer read(connectionHandler, flag);
    std::thread th1(&ReadFromServer::run ,&read);

    th1.join();
    th2.join();
    delete flag;
    return 0;
}
