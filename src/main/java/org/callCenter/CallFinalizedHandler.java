package org.callCenter;

import org.call.Call;
import org.callCenter.operator.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class CallFinalizedHandler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(CallFinalizedHandler.class);
    private final Queue<Operator> operatorsQueue;
    private final Queue<Future<Operator>> processedCall;
    private final AtomicBoolean stop;

    public CallFinalizedHandler(Queue<Operator> operatorsQueue, Queue<Future<Operator>> processedCall, AtomicBoolean stop) {
        this.operatorsQueue = operatorsQueue;
        this.processedCall = processedCall;
        this.stop = stop;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CallFinThread");
        while (!stop.get()) {

            Future<Operator> futureCallProcessing = processedCall.poll();
            //is there some callFutureProcessing

            if (futureCallProcessing != null) {
                //check if call already processed
                if (futureCallProcessing.isDone()) {
                    try {
                        Operator operator = futureCallProcessing.get();
                        //add call for served database in future
                        Call call = operator.getCall();
                        LOG.info("FINISHED Call (id={}) processed by operator (id={}) \n " +
                                        "Call started at {} phoned to CallCenter at {} processed at {} Call status is={}",
                                call.getCallId(), operator.getOperatorId(),
                                call.getStartCallTime(), call.getPhonedToCallCenterTime(), call.getEndCallTime(), call.getStatus());
                        //return operator in operatorQueue
                        System.out.println("Operator serveCallId="+call.getCallId()+" return to queue OperatorId="+operator.getOperatorId());
                        if (!operatorsQueue.offer(operator)) {
                            LOG.error("Can't return operator to operatorQueue operatorId={}", operator.getOperatorId());
                        }

                    } catch (Exception e) {
                        LOG.error("Error during getOperator from Future<Operators>");
                        e.printStackTrace();
                    }
                } else {
                    //put it back to ProcessedQueue
                    if (!processedCall.offer(futureCallProcessing)) {
                        LOG.error("Can't put back FutureCall in ProcessedCallQueue");
                    }
                }
            }
        }
    }
}
