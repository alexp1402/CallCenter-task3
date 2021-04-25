package org.callCenter.operator.operator.operator;

import org.callCenter.operator.OperatorsQueueFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class OperatorsFactoryTest {

    static Stream<Arguments> argumentsForFactory() {
        return Stream.of(
                Arguments.of("OperatorsCount=0",0,1),
                Arguments.of("OperatorsCount=-1",-1,1),
                Arguments.of("serveTimeForCall=0",1,0),
                Arguments.of("serveTimeForCall=-1",1,-1)
        );
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("argumentsForFactory")
    public void GetFactoryWrongTest(String test, int operCount, int serveTime){
        System.out.println(test);
        Assertions.assertThrows(IllegalArgumentException.class, ()-> OperatorsQueueFactory.getOperatorsQueue(operCount,serveTime));
    }

    @Test
    public void GetQueueFromFactory(){
        Assertions.assertNotNull(OperatorsQueueFactory.getOperatorsQueue(5,5000));
    }
}
