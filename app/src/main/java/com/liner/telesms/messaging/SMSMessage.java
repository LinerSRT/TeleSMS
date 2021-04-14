package com.liner.telesms.messaging;

import android.telephony.SmsManager;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SMSMessage implements Serializable {
    private final String number;
    private final String text;
    private final long time;
    private final int simID;


    public SMSMessage(String number, String text, long time, int simID) {
        this.number = number;
        this.text = text;
        this.time = time;
        this.simID = simID;
    }

    public String getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }

    public int getSimID() {
        return simID;
    }


    @NotNull
    @Override
    public String toString() {
        return "Message{" +
                "number='" + number + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", simID=" + simID +
                '}';
    }

    public static void send(String number, String text) {
        SmsManager.getDefault().sendTextMessage(number, null, text, null, null);
    }
}
