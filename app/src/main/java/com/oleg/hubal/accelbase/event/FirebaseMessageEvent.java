package com.oleg.hubal.accelbase.event;

/**
 * Created by User on 11.01.2017.
 */

public class FirebaseMessageEvent {

    public final String messageBody;

    public FirebaseMessageEvent(String messageBody) {
        this.messageBody = messageBody;
    }
}
