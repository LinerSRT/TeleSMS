package com.liner.telesms.telegram;

import com.liner.telesms.Constant;
import com.liner.telesms.messaging.SMSMessage;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeleSMSBot {
    private final TelegramBot bot;

    public TeleSMSBot(Callback callback) {
        bot = new TelegramBot(Constant.TELEGRAM_BOT_TOKEN);
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                Message message = update.message();
                if (message != null)
                    callback.onUpdate(message);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void sendMessage(long chatID, String text, boolean disableNotification){
        bot.execute(new SendMessage(chatID, text).disableNotification(disableNotification));
    }

    public void sendSMS(long chatID, SMSMessage smsMessage){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault());
        String stringBuilder =
                "\uD83D\uDCE9У вас новое сообщение!" + "\n " +
                "\uD83D\uDC64От: " + smsMessage.getNumber() + "\n " +
                        "⏱Получено: "+simpleDateFormat.format(new Date(smsMessage.getTime())) + "\n " +
                "\uD83D\uDCDDТекст сообщения: " + "\n " + "\n " +
                smsMessage.getText();
        sendMessage(chatID, stringBuilder, false);
    }

    public interface Callback {
        void onUpdate(Message message);
    }
}
