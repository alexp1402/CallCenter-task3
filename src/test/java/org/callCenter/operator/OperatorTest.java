package org.callCenter.operator.operator.operator;

import org.call.Call;
import org.callCenter.operator.Operator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OperatorTest {
    //public Operator(int serveCallTimeSeed) {
    @Test
    public void CreateOperatorWithServeTimeLessThenZeroTest(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->new Operator(-1));
    }
    @Test
    public void CreateOperatorTest(){
        Assertions.assertNotNull(new Operator(1000));
    }

    @Test
    public void OperatorIdTest(){
        Operator operator = new Operator(1000);
        Operator operator2 = new Operator(1000);
        Assertions.assertNotEquals(operator.getOperatorId(), operator2.getOperatorId());
    }

    @Test
    public void SetGetCallTest(){
        Operator operator = new Operator(1000);
        Call call = new Call(1);
        operator.setCall(call);
        Assertions.assertEquals(1, operator.getCall().getClientId());
    }

    @Test
    public void Test(){}

    @Test
    public void ServeCallRegularTest(){
        Operator operator = new Operator(1000);
        Call call = new Call(1);
        call.setStatus(Call.ACTIVE);
        operator.setCall(call);
        operator.serveCall();
        Assertions.assertEquals(Call.PROCESSED, call.getStatus().get());
    }


}
