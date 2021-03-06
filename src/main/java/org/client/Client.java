package org.client;

import org.call.Call;
import org.callCenter.CallCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Client implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private final CallCenter callCenter;
    private final static AtomicInteger nextId = new AtomicInteger();
    private final int clientId;
    private Call call;
    private final Random random = new Random();


    public Client(CallCenter callCenter) {
        if (callCenter == null) {
            LOG.error("Wrong arguments for Client callCenter=null");
            throw new IllegalArgumentException("Wrong arguments for Client");
        }
        this.callCenter = callCenter;

        clientId = nextId.getAndIncrement();
        call = new Call(clientId);
        LOG.info("Create client id=" + clientId);
    }

    @Override
    public void run() {
        while (!call.getStatus().compareAndSet(Call.PROCESSED, Call.PROCESSED)) {
            //make call
            makeCall();
            //try to stop waiting call by client
            stopCall();
        }
        //LOG.info("Client id={} stopped call id={}",clientId,call.getCallId());
    }

    public void makeCall() {
        //here no need in lock because only client know about new created call
        if (call.getStatus().compareAndSet(Call.PREPARED, Call.PREPARED)) {
            call.setStartCallTime(LocalDateTime.now());
            call.setStatus(Call.ACTIVE);
            LOG.info("Client id=" + clientId + " make call id=" + call.getCallId());
            callCenter.receiveCall(call);
        }

    }

    public void stopCall() {
        //if call has waiting status and we receive call lock
        if (call.getStatus().compareAndSet(Call.WAITING, Call.WAITING)) {
            if (call.getLock().tryLock()) {
                int stop = random.nextInt(100);
                if (stop >= 50 && stop <= 60) {
                    call.setEndCallTime(LocalDateTime.now());
                    call.getStatus().set(Call.STOPPED_BY_CLIENT);
                }
                call.getLock().unlock();
                LOG.info("CLIENT id={} STOPPED CALL id{} BY HIM SELF stop={} [50-60]", clientId, call.getCallId(), stop);
                try {
                    //waiting for 1 sec and then start new call
                    Thread.sleep(random.nextInt(1000));
                    call = new Call(clientId);
                } catch (InterruptedException e) {
                    LOG.error("InterruptedException during client stop call and waiting event");
                    e.printStackTrace();
                }
            }
        }
    }
}

