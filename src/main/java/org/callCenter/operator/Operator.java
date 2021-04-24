package org.callCenter.operator;

import org.call.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Operator implements Callable<Operator> {

    private static final Logger LOG = LoggerFactory.getLogger(Operator.class);
    private final int serveCallTimeSeed;

    private Call call;

    //to dell
//    Queue<Operator> operatorQueue;


    private final static AtomicInteger nextId = new AtomicInteger();
    private final int operatorId;

    public Operator(int serveCallTimeSeed) {
        //init
        if (serveCallTimeSeed <= 0) {
            LOG.error("Time to serve call must bee greater then zero You enter={} time", serveCallTimeSeed);
            throw new IllegalArgumentException("Time to serve call must bee greater then zero You enter time");
        }
        this.serveCallTimeSeed = serveCallTimeSeed;
        operatorId = nextId.getAndIncrement();
        LOG.info("Operators id={} created", operatorId);
    }

    public void serveCall() {
        if (call == null) {
            LOG.error("Error in Operator - Call is null You remember to set call for operator");
        } else {
            try {
                //ask for Call lock
                if (call.getLock().tryLock()) {

                    call.setStatus(Call.PROCESSING);
                    LOG.info("Operator id={} begin to process call id={}", operatorId, call.getCallId());

                    //serve call for serveCallTime
                    int serveTime = new Random().nextInt(serveCallTimeSeed);
                    try {
                        Thread.sleep(serveTime);
                    } catch (InterruptedException e) {
                        LOG.error("InterruptedError occurred in serveCall={} by Operator={} method", call.getCallId(), operatorId);
                        e.printStackTrace();
                    }

                    call.setEndCallTime(LocalDateTime.now());
                    call.setStatus(Call.PROCESSED);

                    LOG.info("The call id={} is served by operator id={} for time={}", call.getCallId(), operatorId, serveTime);
                } else {
                    LOG.debug("Some one already use call We (operatorId={}) can't obtain call (callId={}) inner lock",operatorId,call.getCallId());
                }
            } finally {
                call.getLock().unlock();
            }
        }
    }

    @Override
    public Operator call() throws Exception {
        serveCall();
        return this;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public Call getCall() {
        return call;
    }
}
