package org.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.callCenter.CallCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientLading implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ClientLading.class);
    private final ExecutorService executor;
    private final CallCenter callCenter;
    private final int ladingWaitingTime;
    private final AtomicBoolean stop;

    public ClientLading(int clientPerSecond, CallCenter center, AtomicBoolean stop) {
        if(clientPerSecond<=0 || center==null  || stop==null){
            LOG.error("Wrong arguments for ClientLading perSec={} stop={} or center=null",clientPerSecond,stop);
            throw new IllegalArgumentException("Wrong arguments for ClientLading");
        }

        callCenter = center;
        ladingWaitingTime = 1000 / clientPerSecond; // 1 sec div client per second
        this.stop = stop;

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("ClientLadingPool %d")
                .build();

        executor = Executors.newCachedThreadPool(threadFactory);

        LOG.info("ClientLading with {} client per second created",clientPerSecond);
    }

    @Override
    public void run() {
        LOG.info("Client lading start");
        Thread.currentThread().setName("ClientLadingThread");
        while (!stop.get()) {

            Client client = new Client(callCenter);
            executor.execute(client);

            //sleep for 1sec/clientPerSec
            try {
                Thread.sleep(ladingWaitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //get stop
        List<Runnable> reject = executor.shutdownNow();
        LOG.info("Client Lading STOPPED with {} rejected task",reject.size());
    }

}
