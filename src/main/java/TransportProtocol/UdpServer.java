package TransportProtocol;

import bank.Bank;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpServer extends Thread{
    private final int PORT = 7020;
    private byte[] buffer;
    private DatagramSocket serverSocket;
    private Bank bank;
    private boolean running;

    public UdpServer(Bank bank) throws SocketException{
        this.bank = bank;
        this.serverSocket = new DatagramSocket(bank.getPort());
        this.running = true;
    }

    private String ReceiveMessage(DatagramSocket socket){
        String message = null;
        buffer = new byte[1024];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        try{
            socket.receive(request);
            message = new String(request.getData(), 0, request.getLength());
        } catch (IOException ignored) {
            System.err.println("not be able to catch message ");
            System.out.println(ignored.getMessage());
        }
        return message;
    }

    @Override
    public void run() {
        while (running) {
            if(bank.isBankrupt()&&(this.bank.getPortfollio()<0)) break;
            String message = ReceiveMessage(serverSocket);
            this.evaluateData(message);
            //serverSocket.send(evaluateData(message, request.getAddress(), request.getPort()));
        }
        serverSocket.close();
    }

    //private DatagramPacket evaluateData(String message, InetAddress address, int port) {
    private void evaluateData(String message) {
        String[] requestArray = message.split(",");

        int quantity = Integer.parseInt(requestArray[0]);

        String abbreviation = requestArray[1];
        double price = Double.parseDouble(requestArray[2]);
        double oldTotalStockPrice = this.bank.updateStockTotalPrice();
        this.bank.addStockAndQuantity(abbreviation,quantity,price);
        double newTotalStockPrice = this.bank.updateStockTotalPrice();

        double oldPortfolio = this.bank.getPortfollio();
        double newPortfolio = oldPortfolio - oldTotalStockPrice + newTotalStockPrice;
        this.bank.setPortfollio(newPortfolio);
        double difference = Math.round((newPortfolio - oldPortfolio) * 100.0) / 100.0;
        if(difference >0 ){
            System.out.println("The Bank receives "+ difference+"$ more than last time");
        }
        else{
            System.out.println("The Bank loses "+ (-difference)+"$ than last time");
        }
        System.out.println("Banks current value: "+ this.bank.getPortfollio());
    }

    public DatagramPacket error(InetAddress address, int port) {
        return new DatagramPacket("error".getBytes(), "error".length(), address, port);
    }

    /*public DatagramPacket reply(InetAddress address, int port) {
        UDPMessage message = getMessage();
        return new DatagramPacket(message.getPayload(), message.length(), address, port);
    }*/
}
