package bgu.spl.net.impl.Bgs;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Bgs.Commands.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

enum comType {
    Register, Login, Logout, Follow, Post, PM, UserList, Stat
}

public class CommandEncDec implements MessageEncoderDecoder<Command> {
    //fields
    private final int UNKNOWN = 999;
    private int DelimiterCounter;
    private int opCodeCounter;
    private byte[] bytes = new byte[1 << 10];
    private byte[] opCode = new byte[2];
    private byte [] numOfFoll = new byte[2];
    private int counterFoll;
    private int len = 0;
    private comType myType;

    public CommandEncDec() {
        opCodeCounter = 0;
        DelimiterCounter = -1;
        counterFoll=-1;
    }

    @Override
    public Command decodeNextByte(byte nextByte) {
        //First we read the op code
        if (opCodeCounter < 1) {
            pushOpCode(nextByte);
        }
        else {
            if(opCodeCounter==1){
                pushOpCode(nextByte);
                //Now we find the command type

                if (DelimiterCounter == -1) {
                    myType = comType.values()[bytesToShort(opCode) - 1];
                    DelimiterCounter = setDelimiterCount(myType);
                }
                //if not need anything else
                if (DelimiterCounter == 0) {
                    return endCommand(myType);

                }
            }

            //Start reading the rest of the command
            else {
                //if not follow command
                if (DelimiterCounter != UNKNOWN) {
                    pushByte(nextByte);
                    if (nextByte == '\0') {
                        DelimiterCounter--;
                        if (DelimiterCounter == 0) {
                            return endCommand(myType);
                        }
                    }
                }
                else{
                    //count the extra bytes for the follow command
                    if(counterFoll==-1) {
                        pushByte(nextByte);
                        counterFoll++;
                    }
                    else if(counterFoll<2){
                        numOfFoll[counterFoll] = nextByte;
                        counterFoll++;
                        pushByte(nextByte);
                    }
                    //when finish read the extra bytes, update the delimiters number
                    if(counterFoll==2)
                        DelimiterCounter = bytesToShort(numOfFoll);
                }


            }

        }

        return null;
    }

    private void pushOpCode(byte nextByte) {
        opCode[opCodeCounter] = nextByte;
        opCodeCounter++;
        pushByte(nextByte);
    }

    private Command endCommand(comType d) {
        Command output = BuildCommand(d, bytes, len-1, StandardCharsets.UTF_8);
        DelimiterCounter = -1;
        opCodeCounter = 0;
        len = 0;
        counterFoll =-1;
        myType=null;
        return output;
    }

    private Command BuildCommand(comType d, byte[] bytes, int len, Charset utf8) {
        switch (d) {
            case Register:
                return new Register(bytes, len, utf8);
            case Login:
                return new Login(bytes, len, utf8);
            case Logout:
                return new Logout(bytes, len, utf8);
            case Post:
                return new Post(bytes, len, utf8);
            case PM:
                return new PM(bytes, len, utf8);
            case Stat:
                return new Stat(bytes, len, utf8);
            case Follow:
                return new Follow(bytes, len, utf8);
            case UserList:
                return new UserList(bytes, len, utf8);


        }
        return null;
    }

    private int setDelimiterCount(comType d) {
        switch (d) {
            case Register:
                return 2;
            case Login:
                return 2;
            case Logout:
                return 0;
            case Post:
                return 1;
            case PM:
                return 2;
            case Stat:
                return 1;
            case Follow:
                return UNKNOWN;
            case UserList:
                return 0;


        }
        return -1;
    }


    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }


    @Override
    public byte[] encode(Command message) {
        return message.getBytes();
    }





    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }
}
