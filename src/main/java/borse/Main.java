package borse;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

/**
 * Serves as the entrypoint to the Gateway.
 * Parses the arguments passed in the docker-compose file
 * into the corresponding addresses and ports.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        try{
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
        }
        int handlerCount = args.length/2;
        BankHandler[] handlers = new BankHandler[handlerCount];
        DatagramSocket receiver = new DatagramSocket();
        int pos =0;
        for(int i = 0;i< args.length-2;i+=2){
            handlers[pos]= new BankHandler(InetAddress.getByName(args[i]),Integer.parseInt(args[i+1]),
                receiver);
            pos++;
        }

        Borse borse= new Borse(args[args.length-1],handlers);
        borse.startBroadcastingData(10000);
    }
}
