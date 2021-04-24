package org.callCenter.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OperatorsQueueFactory {
    private static final Logger LOG = LoggerFactory.getLogger(OperatorsQueueFactory.class);

    public static Queue<Operator> getOperatorsQueue(int operatorsCount, int serveCallTime){
        if (operatorsCount<1){
            LOG.error("Operators count can't be less then 1 (your count is {})",operatorsCount);
            throw new IllegalArgumentException("Operators count can't be less then 1");
        }
        Queue<Operator> operatorQueue = new ConcurrentLinkedQueue<>();

        for(int i=0;i<operatorsCount;i++){
           if (!operatorQueue.offer(new Operator(serveCallTime))){
               LOG.error("ERROR in FACTORY OPERATOR Can't add new operator to queue");
           }
        }
        LOG.info("Operators queue with {} operators created", operatorsCount);
        return operatorQueue;
    }

}
