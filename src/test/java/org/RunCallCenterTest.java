package org;

import org.callCenter.CallCenter;
import org.client.ClientLading;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class RunCallCenterTest {
    @Test
    public void runCallCenter() throws InterruptedException {
        AtomicBoolean stop = new AtomicBoolean(false);
        int operatorServeCallTimeMAX = 4000;

        CallCenter center = new CallCenter(5, operatorServeCallTimeMAX, stop);

        new Thread(center).start();

        ClientLading clients = new ClientLading(2,center,stop);
        new Thread(clients).start();

        while(true) {
            Thread.sleep(20000);
        }
       //stop.set(true);
    }
}
