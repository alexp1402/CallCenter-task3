package org.callCenter;

import org.callCenter.operator.Operator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CallFinalizedHandlerTest {

    static Stream<Arguments> argumentsForCFH() {
        return Stream.of(
                Arguments.of("Wrong argument operatorsQueue=null",null,new ConcurrentLinkedQueue<Future<Operator>>(), new AtomicBoolean(true)),
                Arguments.of("Wrong argument processedQueue=null",new ConcurrentLinkedQueue<Operator>(),null, new AtomicBoolean(true)),
                Arguments.of("Wrong argument operatorsQueue=null",new ConcurrentLinkedQueue<Operator>(),new ConcurrentLinkedQueue<Future<Operator>>(), null)
        );
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("argumentsForCFH")
    public void WrongCallFinalizerCreatingTest(String name, Queue<Operator> operatorQ, Queue<Future<Operator>> processedQ, AtomicBoolean stop){
        Assertions.assertThrows(IllegalArgumentException.class, ()->new CallFinalizedHandler(operatorQ, processedQ,stop));
    }

    @Test
    public void CallFinalizedHandlerTest(){
        Assertions.assertNotNull(new CallFinalizedHandler(new ConcurrentLinkedQueue<Operator>(), new ConcurrentLinkedQueue<Future<Operator>>(), new AtomicBoolean(true)));
    }
}
