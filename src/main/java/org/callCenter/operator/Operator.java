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

    public boolean serveCall() {

            if (call == null) {
                LOG.error("Error in Operator - Call is null You remember to set call for operator");
                throw new IllegalStateException("Error in Operator - Call is null You remember to set call for operator");
            }

            //if stopped by client return operator to queue
            if (call.getStatus().compareAndSet(Call.STOPPED_BY_CLIENT, Call.STOPPED_BY_CLIENT)) {
                return true;
            }

            //ask for Call lock
            if (call.getLock().tryLock()) {

                LOG.info("Operator id={} begin to process call id={}", operatorId, call.getCallId());
                //serve call for serveCallTime
                int serveTime = new Random().nextInt(serveCallTimeSeed);
                try {
                    Thread.sleep(serveTime);
                } catch (InterruptedException e) {
                    //when stop became true InterruptedException is (because stop forced)
                    LOG.error("InterruptedError occurred in serveCall={} by Operator={} method", call.getCallId(), operatorId);
                    e.printStackTrace();
                }

                call.setEndCallTime(LocalDateTime.now());
                call.setStatus(Call.PROCESSED);
                call.getLock().unlock();

                //LOG.info("The call id={} is served by operator id={} for time={}", call.getCallId(), operatorId, serveTime);
                return true;
            } else {
                LOG.debug("Some one already use call We (operatorId={}) can't obtain call (callId={}) inner lock Call status={}", operatorId, call.getCallId(),call.getStatus());
                return false;
            }

        }

        @Override
        public Operator call () throws Exception {
            while (true) {
                if (serveCall()) {
                    return this;
                }
            }
        }

        public int getOperatorId () {
            return operatorId;
        }

        public void setCall (Call call){
            this.call = call;
        }

        public Call getCall () {
            return call;
        }
    }
