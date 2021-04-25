package org.client;

import org.call.Call;
import org.callCenter.CallCenter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import org.mockito.Mockito;

public class ClientTest {

    static CallCenter callCenter;

    @BeforeAll
    static void createCallCenterMock(){
        callCenter = Mockito.mock(CallCenter.class);
    }


    @Test
    public void CreateClientTest(){
        Client client = new Client(callCenter);
        Assertions.assertNotNull(client);
    }
    @Test
    public void CreateWrongClientTest(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->new Client(null));
    }

}
