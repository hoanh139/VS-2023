package TransportProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {

    public void SendMessage(DatagramSocket socket, String messages, InetAddress address, int port){
        try{
            DatagramPacket request = new DatagramPacket(messages.getBytes(), messages.length(), address, port);
            socket.send(request);
        } catch (IOException ignored) {}
    }
}
