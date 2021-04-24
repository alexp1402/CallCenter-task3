package org.call;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Call {

    //call status
    public final static int PREPARED = 10;
    public final static int ACTIVE = 20;
    public final static int PROCESSING = 30;
    public final static int WAITING = 40;
    public final static int STOPPED_BY_CLIENT = 50;
    public final static int PROCESSED = 60;

    //call timestamp
    private LocalDateTime startCallTime = null;
    private LocalDateTime phonedToCallCenterTime = null;
    private LocalDateTime endCallTime = null;


    private final AtomicInteger status;

    //for Call id generation
    private final static AtomicInteger nextId = new AtomicInteger();

    //Lock
    private final ReentrantLock lock;

    private final int callId;

    private final int clientId;

    public Call(int clientId) {
        callId = nextId.getAndIncrement();
        this.clientId = clientId;
        status = new AtomicInteger(Call.PREPARED);
        lock = new ReentrantLock();
    }

    public LocalDateTime getStartCallTime() {
        return startCallTime;
    }

    public void setStartCallTime(LocalDateTime startCallTime) {
        this.startCallTime = startCallTime;
    }

    public LocalDateTime getPhonedToCallCenterTime() {
        return phonedToCallCenterTime;
    }

    public void setPhonedToCallCenterTime(LocalDateTime phonedToCallCenterTime) {
        this.phonedToCallCenterTime = phonedToCallCenterTime;
    }

    public LocalDateTime getEndCallTime() {
        return endCallTime;
    }

    public void setEndCallTime(LocalDateTime endCallTime) {
        this.endCallTime = endCallTime;
    }

    public AtomicInteger getStatus() {
        return status;
    }

    public void setStatus(int newStatus) {
        status.set(newStatus);
    }

    public int getCallId() {
        return callId;
    }

    public int getClientId() {
        return clientId;
    }

    public ReentrantLock getLock() {

        return lock;
    }
}
