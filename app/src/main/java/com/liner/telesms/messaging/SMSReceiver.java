package com.liner.telesms.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liner.telesms.Constant;
import com.liner.telesms.utils.Broadcast;
import com.liner.telesms.utils.PM;

import java.util.ArrayList;
import java.util.List;

import static com.liner.telesms.messaging.SIMUtils.getSimID;

public class SMSReceiver extends BroadcastReceiver {
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String PDU = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle == null)
                return;
            Object[] pdu = (Object[]) bundle.get(PDU);
            if (pdu == null)
                return;
            PM.init(context);
            List<SMSMessage> SMSMessageList = new ArrayList<>();
            int simID = bundle.containsKey("simId") ?
                    bundle.getInt("simId") : (bundle.containsKey("subscription") ?
                    getSimID(context, bundle.getInt("subscription")) : 1
            );
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                for (Object pdus : pdu) {
                    SMSMessage SMSMessage = processMessage(SmsMessage.createFromPdu((byte[]) pdus, intent.getStringExtra("format")), simID);
                    if (SMSMessage != null)
                        SMSMessageList.add(SMSMessage);

                }
            } else {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    SMSMessage SMSMessage = processMessage(smsMessage, simID);
                    if (SMSMessage != null)
                        SMSMessageList.add(SMSMessage);
                }
            }
            PM.putList(Constant.KEY_MESSAGES_LIST, SMSMessageList);
        }
    }

    @Nullable
    private SMSMessage processMessage(@NonNull SmsMessage smsMessage, int simID) {
        String number = smsMessage.getOriginatingAddress();
        String text = smsMessage.getMessageBody();
        if (number != null) {
            SMSMessage SMSMessage = new SMSMessage(number, text, smsMessage.getTimestampMillis(), simID);
            Intent intent = new Intent(Constant.ACTION_DEBUG_SEND_SMS);
            intent.putExtra("message", SMSMessage);
            Broadcast.send(intent);
            return SMSMessage;
        }
        return null;
    }

}
