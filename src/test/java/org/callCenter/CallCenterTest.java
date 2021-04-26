package org.callCenter;

import org.call.Call;
import org.call.CallTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CallCenterTest {

    static Stream<Arguments> argumentsForCC() {
        return Stream.of(
                Arguments.of("Wrong operators count = -1",-1,100, new AtomicBoolean(true)),
                Arguments.of("Wrong serveTimeMax = 0",2,0, new AtomicBoolean(true)),
                Arguments.of("Wrong serveTimeMax = -1",2,-1, new AtomicBoolean(true)),
                Arguments.of("Wrong stop=null",1,100, null)
                );
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("argumentsForCC")
    public void WrongCallCenterCreatingTest(String name, int operCount, int timeMax, AtomicBoolean stop) {
        Assertions.assertThrows(IllegalArgumentException.class, ()->new CallCenter(operCount,timeMax,stop));
    }

    @Test
    public void CallCenterCreatingTest(){
        Assertions.assertNotNull(new CallCenter(2,2,new AtomicBoolean(true)));
    }

}
