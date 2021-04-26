package org.callCenter;

import org.call.Call;
import org.callCenter.operator.Operator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CallProcessorHandlerTest {

    static Queue<Call> callQ = new ConcurrentLinkedQueue<>();
    static Queue<Operator> operatorQ = new ConcurrentLinkedQueue<>();
    static Queue<Future<Operator>> processedC = new ConcurrentLinkedQueue<>();
    static ExecutorService exec = Executors.newCachedThreadPool();
    static AtomicBoolean stop = new AtomicBoolean(true);

    static Stream<Arguments> argumentsForCPH() {
        return Stream.of(
                Arguments.of("Wrong argument callsQueue=null", null, operatorQ, processedC, exec, stop),
                Arguments.of("Wrong argument operatorsQueue=null", callQ, null, processedC, exec, stop),
                Arguments.of("Wrong argument processedQueue=null", callQ, operatorQ, null, exec, stop),
                Arguments.of("Wrong argument executors=null", callQ, operatorQ, processedC, null, stop),
                Arguments.of("Wrong argument stop=null", callQ, operatorQ, processedC, exec, null)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("argumentsForCPH")
    public void WrongCreatingCallProcessorHandlerTest(String name, Queue<Call> callsQueue, Queue<Operator> operatorsQueue,
                                                     Queue<Future<Operator>> processedCall, ExecutorService executors,
                                                     AtomicBoolean stop) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CallProcessorHandler(callsQueue, operatorsQueue, processedCall, executors, stop));
    }

    @Test
    public void CreatingCallProcessorHandlerTest(){
        Assertions.assertNotNull( new CallProcessorHandler(callQ, operatorQ, processedC, exec, stop));
    }
}
