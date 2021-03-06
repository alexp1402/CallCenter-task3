package org.callCenter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.call.Call;
import org.callCenter.operator.Operator;
import org.callCenter.operator.OperatorsQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class CallCenter implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CallCenter.class);
    private final AtomicBoolean stop;


    private final Queue<Call> incomingCallQueue;
    private final Queue<Operator> operatorQueue;
    private final Queue<Future<Operator>> processedCall;
    private final ExecutorService executors;

    private final CallProcessorHandler callProcessor;
    private final CallFinalizedHandler callFinalizer;
    private Call incomingCall;


    public CallCenter(int operatorsCount, int serveCallTimeMAX, AtomicBoolean stop) {
        if ((operatorsCount < 1) || (serveCallTimeMAX<=0) || stop==null) {
            LOG.error("Wrong arguments for CallCenter creating operatorCount={} serveMaxTime={} stop={}", operatorsCount,serveCallTimeMAX,stop);
            throw new IllegalArgumentException("Wrong arguments for CallCenter creating");
        }

        this.stop = stop;

        //init create OperatorsQueue
        operatorQueue = OperatorsQueueFactory.getOperatorsQueue(operatorsCount, serveCallTimeMAX);
        //init incomingQueue
        incomingCallQueue = new ConcurrentLinkedQueue<>();
        //init processedQueue
        processedCall = new ConcurrentLinkedQueue<>();
        //init executorService
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("CallCenterPool %d")
                .build();
        executors = Executors.newCachedThreadPool(threadFactory);

        //init CallProcessor service
        callProcessor = new CallProcessorHandler(incomingCallQueue, operatorQueue, processedCall, executors, stop);
        //init CallFinalizer service
        callFinalizer = new CallFinalizedHandler(operatorQueue, processedCall, stop);

        //start callProcessor in Thread
        executors.execute(callProcessor);
        //start callFinalizedHandler
        executors.execute(callFinalizer);

        LOG.info("##### CALLCENTER START WORK #####");
    }

    public void receiveCall(Call call) {
        //receive and set call like incomingCall
        if (incomingCall == null) {
            incomingCall = call;
        } else {
            //here must be queue because we can receive more then one call in one time
            LOG.error("CANNOT TAKE CALL (id={}) BECAUSE incomingCALL is not null IncomingCallQueue size={} OperatorQueue size={} ProcessedQueue size={}", call.getCallId(), incomingCallQueue.size(), operatorQueue.size(), processedCall.size());
            //throw new RuntimeException("BUSY INCOMING CALL");
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CallCenterThread");
        try {
            while (!stop.get()) {
                if (incomingCall != null) {
                    incomingCall.setPhonedToCallCenterTime(LocalDateTime.now());
                    //new incoming call try to process. Check for free operator
                    Operator operator = operatorQueue.poll();
                    if (operator != null && incomingCall.getLock().tryLock()) {
                        incomingCall.setStatus(Call.PROCESSING);
                        operator.setCall(incomingCall);
                        LOG.info("Call (id={}) start to process DIRECTLY by operator (id={})", incomingCall.getCallId(), operator.getOperatorId());
                        incomingCall.getLock().unlock();
                        Future<Operator> callProcessing = executors.submit(operator);
                        // add future to processedQueue
                        if (!processedCall.offer(callProcessing)) {
                            LOG.error("An error occurred in CallCenter when we try to offer Future call to ProcessedCallQueue");
                        }
                    } else {
                        //transfer call throughout incomingCallQueue
                        incomingCall.setStatus(Call.WAITING);
                        if (!incomingCallQueue.offer(incomingCall)) {
                            LOG.error("An error occurred in CallCenter when we try to offer call to incomingCallQueue");
                        }
                        LOG.info("CallCenter receive Call id={} and put it into Queue", incomingCall.getCallId());
                    }
                    incomingCall = null;
                }
            }
        } finally {
            if (!executors.isShutdown()) {
                List<Runnable> rejected = executors.shutdownNow();
                LOG.info("Operators thread pool stopped with {} rejected task", rejected.size());
            }
        }
    }
}
