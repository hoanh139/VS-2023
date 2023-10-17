package bank;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import thrift.BankService;
import thrift.LoanRequest;
import thrift.LoanResponse;

public class RPCHandler extends Thread implements BankService.Iface {
    private Bank bank;
    private TServer server;
    public RPCHandler(Bank bank){this.bank = bank;}

    @Override
    public void run() {
        startServer();
    }

    public void startServer(){
        String tmp = System.getenv("RPCPORT");
        if(tmp != null) {
            int rpcPort = Integer.parseInt(tmp);
            TServerTransport transport = null;
            try {
                transport = new TServerSocket(rpcPort);
                server = new TSimpleServer(
                        new TServer.Args(transport).processor(new BankService.Processor<>(this)));
                server.serve();
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public LoanResponse requestLoan(LoanRequest request) throws TException {
        if(this.bank.getPortfollio()> request.getAmount()) {
            this.bank.setPortfollio(this.bank.getPortfollio()- (double)request.getAmount());
            System.out.println("Current Portfolio from " + this.bank.getBankName() + ": " + this.bank.getPortfollio() + "with Amount " + request.getAmount());
            return LoanResponse.APPROVED;
        }
        return LoanResponse.DENIED;
    }
}
