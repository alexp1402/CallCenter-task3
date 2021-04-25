package org.call;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class CallTest {

    @Test
    public void CallTestTime() {
        Call call = new Call(0);
        LocalDateTime time = LocalDateTime.now();
        call.setStartCallTime(time);
        call.setEndCallTime(time);
        call.setPhonedToCallCenterTime(time);
        System.out.println();
        Assertions.assertTrue(time.equals(call.getEndCallTime()));
        Assertions.assertTrue(time.equals(call.getStartCallTime()));
        Assertions.assertTrue(time.equals(call.getPhonedToCallCenterTime()));
    }


    static Stream<Arguments> argumentsForStatus() {
        return Stream.of(
                Arguments.of(Call.PREPARED),
                Arguments.of(Call.ACTIVE),
                Arguments.of(Call.PROCESSED),
                Arguments.of(Call.WAITING),
                Arguments.of(Call.PROCESSING),
                Arguments.of(Call.STOPPED_BY_CLIENT)
        );
    }

    @ParameterizedTest()
    @MethodSource("argumentsForStatus")
    public void CallStatusTest(int status) {
        Call call = new Call(1);
        call.setStatus(status);
        Assertions.assertEquals(status, call.getStatus().get());
    }

    @Test
    public void CallLockTest() {
        Call call = new Call(1);
        if (call.getLock().tryLock()) {
            new Thread(()->{
                Assertions.assertFalse(call.getLock().tryLock());
            }).start();
        } else {
            Assertions.assertTrue(false);
        }
    }

    static Stream<Arguments> argumentsForClientId() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(22),
                Arguments.of(456),
                Arguments.of(0)
        );
    }

    @ParameterizedTest()
    @MethodSource("argumentsForClientId")
    public void CallClientIdTest(int clientId){
        Call call = new Call(clientId);
        Assertions.assertEquals(clientId, call.getClientId());
    }
}
