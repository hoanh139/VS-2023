package bank;

import TransportProtocol.UdpServer;
import borse.Stock;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.eclipse.paho.client.mqttv3.MqttException;
import thrift.BankService;
import thrift.LoanRequest;
import thrift.LoanResponse;
import thrift.BankService.Client;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Sensor which generates random data
 * and communicates using a DatagramSocket.
 */
public class Bank extends Thread   {
    private static final String PREPARE_TOPIC = "prepare";
    private static final String COMMIT_TOPIC = "commit";
    private static final String FINISH_TOPIC = "finish";
    private final UdpServer handler;
    private final String bankName;
    private double portfollio;
    private int HTTP_DEFAULT_PORT;
    private int port;

    private ClientHandler clientHandler;
    private final ServerSocket serverSocket;

    private HashMap<Stock, Integer> stockList;
    private boolean running;

    private HashMap<String, Integer> rpcAdress;
    private boolean bankrupt;
    private RPCHandler rpcHandler;

    private MQTTHandler mqttHandler;


    public Bank(String name, int port, int httpPort) throws IOException {
        this.HTTP_DEFAULT_PORT= httpPort;
        this.bankName = name;
        this.portfollio = 0;
        stockList = new HashMap<>();
        this.port = port;
        this.running = true;
        serverSocket = new ServerSocket(HTTP_DEFAULT_PORT);
        this.bankrupt = false;
        rpcHandler = new RPCHandler(this);
        String bankRPCs = System.getenv("RPCBANKS");

        if (bankRPCs != null) {
            rpcAdress= new HashMap<>();
            String[] bankEndpoints = bankRPCs.split(",");
            for (String endpoint : bankEndpoints) {
                String[] parts = endpoint.split(":");
                String bankName = parts[0];
                int portName = Integer.parseInt(parts[1]);
                rpcAdress.put(bankName,portName);
            }
        }

        mqttHandler = new MQTTHandler(this);
        try{
            mqttHandler.connectMQTT();
        }
        catch (MqttException me){
            System.out.println("MQTT Exception");
            throw new RuntimeException(me);
        }

        this.handler = new UdpServer(this);

    }
    public boolean isBankrupt() {
        return bankrupt;
    }
    public void setBankrupt(boolean bankrupt) {
        this.bankrupt = bankrupt;
    }
    @Override
    public void run() {
        handler.start();
        rpcHandler.start();

        try {
            while (running) {
                if(this.getPortfollio()<0){
                    //askForHelp();
                    //if(isBankrupt()){
                    this.setBankrupt(true);
                    System.out.println("HELPP!!!!!!!!!!");
                    String amount = String.valueOf(-this.getPortfollio());
                    mqttHandler.sendMQTTMessage(PREPARE_TOPIC,this.bankName+";"+amount);
                    sleep(1000);
                    if(isBankrupt()){
                        System.out.println("BANKRRUPT!!!!!!!!!!");
                    }
                    //running= false;
                    //}
                    //else {
                    //System.out.println("RESCUE !!!!!!!!!");
                    //}
                }
                Socket client = serverSocket.accept();
                this.clientHandler = new ClientHandler(client, this);
                this.clientHandler.start();
            }
            System.out.println("Bank stop working");
        } catch (Exception ignored) {
        }
    }
    public HashMap<Stock, Integer> getStockList() {
        return stockList;
    }

    public int getPort(){
        return this.port;
    }

    public void addStockAndQuantity(String abbreviation, int quantity, double price){
        Stock tmpStock = null;
        for (Stock stock : stockList.keySet()) {
            if (stock.getAbbreviation().equals(abbreviation)) {
                tmpStock = stock;
            }
        }
        if (tmpStock == null) {
            tmpStock = new Stock(abbreviation, price);
            stockList.put(tmpStock, quantity);
        } else {
            int newQuantity = stockList.get(tmpStock) + quantity;
            double newPrice = price;
            tmpStock.setPrice(newPrice);
            stockList.replace(tmpStock, newQuantity);
        }
    }
    public double updateStockTotalPrice() {
        double value = 0;
        for (Stock stock : stockList.keySet()) {
            value += stock.getPrice() * stockList.get(stock);
        }
        value = Math.round(value * 100.0) / 100.0;
        return value;
    }
    public double getPortfollio() {
        return portfollio;
    }

    public void setPortfollio(double portfollio) {
        this.portfollio = portfollio;
    }

    public TSocket establishConnection(String host, int port) {
        TSocket socket = null;
        while (socket == null) {
            try {
                socket = new TSocket(host, port);
            } catch (Exception ignored) {
            }
        }
        while (!socket.getSocket().isConnected())
            try {
                socket.open();
            } catch (Exception e) {
                System.err.println("Error opening socket");
            }
        return socket;
    }

    /*private void askForHelp() {
        System.out.println("SENDING HELPING REQUEST");
        for (Map.Entry<String, Integer> entry : rpcAdress.entrySet()) {
            String hostRpc = entry.getKey();
            int portRpc = entry.getValue();

            try{
                TTransport transport;
                transport = establishConnection(hostRpc, portRpc);
                TProtocol protocol = new TBinaryProtocol(transport);
                Client client = new Client(protocol);
                Double value = Double.valueOf(-this.getPortfollio());
                LoanRequest request = new LoanRequest(value);
                LoanResponse response = client.requestLoan(request);
                System.out.println("Response: "+ response);
                if(response.equals(LoanResponse.APPROVED)){
                    System.out.println(hostRpc+ " success to rescue");
                    this.setPortfollio(10000);
                    this.setBankrupt(false);
                    break;
                }
                else if(response.equals(LoanResponse.DENIED)){
                    System.out.println(hostRpc+" fail to rescue");
                    setBankrupt(true);
                }
                else {
                    System.out.println("Else case: "+ response);
                }
                transport.close();
            }
            catch (Exception e){
                System.out.println("Error");
                e.printStackTrace();
            }

        }

    }*/
    public void askForHelp() {
        int allAccept=0;
        Double value = Double.valueOf(-this.getPortfollio());
        System.out.println("SENDING HELPING REQUEST");
        for (Map.Entry<String, Integer> entry : rpcAdress.entrySet()) {
            String hostRpc = entry.getKey();
            int portRpc = entry.getValue();

            try{
                TTransport transport;
                transport = establishConnection(hostRpc, portRpc);
                TProtocol protocol = new TBinaryProtocol(transport);
                Client client = new Client(protocol);

                int numberOfBank = rpcAdress.size();
                long startTime = System.nanoTime();
                LoanRequest request = new LoanRequest(value/numberOfBank);
                LoanResponse response = client.requestLoan(request);
                long endTime = System.nanoTime();
                long rtt = endTime - startTime;
                System.out.println("RTT: " + rtt + " nanoseconds");
                System.out.println("Response: "+ response);

                if(response.equals(LoanResponse.APPROVED)){
                    System.out.println(hostRpc+ " success to rescue");
                    allAccept++;
                }
                else if(response.equals(LoanResponse.DENIED)){
                    System.out.println(hostRpc+" fail to rescue");
                }
                else {
                    System.out.println("Else case: "+ response);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(allAccept==rpcAdress.size()){
            this.setPortfollio(0);
            this.setBankrupt(false);
        }

    }

    public String getBankName() {
        return bankName;
    }
}

