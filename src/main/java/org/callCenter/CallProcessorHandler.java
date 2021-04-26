package org.callCenter;

import org.call.Call;
import org.callCenter.operator.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CallProcessorHandler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(CallProcessorHandler.class);
    private Queue<Call> callsQueue;
    private Queue<Operator> operatorsQueue;
    private Queue<Future<Operator>> processedCall;
    private AtomicBoolean stop;
    private ExecutorService executors;

    public CallProcessorHandler(Queue<Call> callsQueue, Queue<Operator> operatorsQueue,
                                Queue<Future<Operator>> processedCall, ExecutorService executors,
                                AtomicBoolean stop) {
        //check incoming params
        if (callsQueue == null || operatorsQueue == null || processedCall == null || executors == null || stop == null) {
            LOG.error("Wrong arguments when creating CallProcessor queueCall={}\n operatorsQueue={}\n" +
                            " processedQueue={}\n executors={}\n stopFlag={}\n",
                    callsQueue, operatorsQueue, processedCall, executors, stop);
            throw new IllegalArgumentException("Wrong arguments when creating CallProcessor");
        }

        this.processedCall = processedCall;
        this.stop = stop;
        this.callsQueue = callsQueue;
        this.operatorsQueue = operatorsQueue;
        this.executors = executors;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CallProcessorThread");
        while (!stop.get()) {

            // 1. is there call in queue
            Call call;
            while ((call = callsQueue.poll()) == null) {
                //wait
            }
            //2. is there "free" operator in queue
            Operator operator;
            while ((operator = operatorsQueue.poll()) == null) {
                //wait
            }

            LOG.info("Operator (id={}) try to serve call(id={}) from Queue", operator.getOperatorId(), call.getCallId());
            if(call.getStatus().compareAndSet(Call.STOPPED_BY_CLIENT,Call.STOPPED_BY_CLIENT)){

            }
            //take inner call lock
            if (call.getLock().tryLock()) {
                    call.setStatus(Call.PROCESSING);
                    operator.setCall(call);
                    call.getLock().unlock();

                Future<Operator> callProcessing = executors.submit(operator);
                if (!processedCall.offer(callProcessing)) {
                    LOG.error("Can't add Future(processingCall) to ProcessedQueue callId={} operatorId={}",
                            call.getCallId(), operator.getOperatorId());
                }

            }
        }
    }
}
