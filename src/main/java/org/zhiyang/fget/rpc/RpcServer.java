package org.zhiyang.fget.rpc;

import hprose.server.HproseTcpServer;
import org.zhiyang.fget.prospector.Spooler;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author lizhiyang
 */
public class RpcServer {

    private Spooler spooler;

    private HproseTcpServer tcpServer;

    public RpcServer(Spooler spooler) {
        this.spooler = spooler;
    }

    public void start() throws URISyntaxException, IOException {

        this.tcpServer = new HproseTcpServer("tcp://0.0.0.0:8081");
        this.tcpServer.setReactorThreads(10);
        this.tcpServer.add(new RpcService(this.spooler));
        this.tcpServer.start();

    }

    public void stop() {
        this.tcpServer.stop();
    }

}
