CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude 
LDFLAGS:=-lboost_system -pthread

all: EchoClient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/ReadFromServer.o bin/WriteToServer.o $(LDFLAGS) 

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/ReadFromServer.o bin/WriteToServer.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	
bin/ReadFromServer.o: src/ReadFromServer.cpp
	g++ $(CFLAGS) -o bin/ReadFromServer.o src/ReadFromServer.cpp
	
bin/WriteToServer.o: src/WriteToServer.cpp
	g++ $(CFLAGS) -o bin/WriteToServer.o src/WriteToServer.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
