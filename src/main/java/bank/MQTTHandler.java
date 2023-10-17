package bank;

import org.eclipse.paho.client.mqttv3.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class MQTTHandler{
    private static final String PREPARE_TOPIC = "prepare";
    private static final String COMMIT_TOPIC = "commit";
    private static final String FINISH_TOPIC = "finish";
    private MqttClient client;

    private Bank bank;

    private double amount;

    private int respones;

    private int bankSize;
    private boolean check;

    public MQTTHandler(Bank bank){
        this.bank = bank;
        amount = 0;
        respones = 0;
        check = true;
        bankSize = 3;
    }

    /*public void run() {
        try {
            connectMQTT();
        }
        catch (MqttException me){
        }
    }*/

    public void connectMQTT() throws MqttException {
        client = createClient("broker",1883);
        client.subscribe(PREPARE_TOPIC);
        client.subscribe(COMMIT_TOPIC);
        client.subscribe(FINISH_TOPIC);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("Connection loss");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                System.out.println("TOPIC:"+ topic);
                if (topic.equals(PREPARE_TOPIC)) {
                    handlePrepareMessage(mqttMessage);
                } else if (topic.equals(COMMIT_TOPIC)) {
                    handleCommitMessage(mqttMessage);
                }
                else if (topic.equals(FINISH_TOPIC)) {
                    handleFinishMessage(mqttMessage);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {


            }
        });
    }

    public MqttClient createClient(String host,int port){
        MqttClient publisher = null;
        String id = UUID.randomUUID().toString();
        while (publisher== null){
            try {
                String ip =InetAddress.getByName(host).toString().substring(host.length()+1);
                publisher= new MqttClient("tcp://"+ip+":"+port,id);

            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
        while(!publisher.isConnected()){
            try {
                publisher.connect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
        return publisher;
    }

    private void handlePrepareMessage(MqttMessage mqttMessage) {
        String split[] = new String(mqttMessage.getPayload()).split(";");
        String name = split[0];
        String money = split[1];
        if(!name.equals(bank.getBankName())){
            this.amount= Double.parseDouble(money)/2;
            sendMQTTMessage(COMMIT_TOPIC, name +";"+(amount< this.bank.getPortfollio()?"true":"false"));
        }
    }

    private void handleCommitMessage(MqttMessage mqttMessage) {
        System.out.println("Handle Commit Message from MQTTHandler from " + this.bank.getBankName() + " " + this.amount);
        String split[] = new String(mqttMessage.getPayload()).split(";");
        String name = split[0];
        String vote = split[1];
        if(name.equals(bank.getBankName())){
            respones++;
            System.out.println(respones);
            if(vote.equals("false")) check = false;
            if(respones== bankSize-1&& check){
                sendMQTTMessage(FINISH_TOPIC, name);
                respones=0;
            }

        }
    }

    private void handleFinishMessage(MqttMessage mqttMessage) {
        System.out.println("Handle Finish Message from MQTTHandler from " + this.bank.getBankName());
        String name = new String(mqttMessage.getPayload());
        if (name.equals(bank.getBankName())) {
            bank.askForHelp();
        }

    }

    public void sendMQTTMessage(String topic, String mess){
        System.out.println("Send mqtt from Bank with message "+ mess +" with topic "+ topic );
        try {
            MqttMessage msg = new MqttMessage();
            msg.setPayload(mess.getBytes());
            msg.setQos(2);
            msg.setRetained(true);
            client.publish(topic,msg);
        } catch (MqttException ignore) {
        }
    }
}
