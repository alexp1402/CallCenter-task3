package org.client;

import org.callCenter.CallCenter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ClientLadingTest {

    static CallCenter callCenter;
    static AtomicBoolean stop = new AtomicBoolean(true);

    @BeforeAll
    static void createCallCenterMock(){
        callCenter = Mockito.mock(CallCenter.class);
    }

    static Stream<Arguments> argumentsForLading() {
        return Stream.of(
                Arguments.of("clientPerSecond<0",-1, callCenter, stop),
                Arguments.of("CallCenter=null",1, null, stop),
                Arguments.of("stop=null",1,callCenter,null)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("argumentsForLading")
    public void CreateLadingWrongTest(String name, int clientPerSec, CallCenter cc, AtomicBoolean st){
        Assertions.assertThrows(IllegalArgumentException.class, ()->new ClientLading(clientPerSec,cc,st));
    }

    @Test
    public void CreateLadingGoodTest(){
        ClientLading cl = new ClientLading(1,callCenter,stop);
        Assertions.assertNotNull(cl);
    }

}
