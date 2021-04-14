package com.liner.telesms.messaging;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class SIMUtils {

    public static int getSimID(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager != null) {
                try {
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
                    List<SubscriptionInfo> list = subscriptionManager.getActiveSubscriptionInfoList();
                    if (list != null && list.size() > 0) {
                        for (SubscriptionInfo subscriptionInfo : list) {
                            if (subscriptionInfo.getSubscriptionId() == id) {
                                return subscriptionInfo.getSimSlotIndex() + 1;
                            }
                        }
                        return 1;
                    }
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;
                }
            }
            return 1;
        }
        return 1;
    }

    public static String getSimCarrier(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager != null) {
                try {
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
                    List<SubscriptionInfo> list = subscriptionManager.getActiveSubscriptionInfoList();
                    if (list != null && list.size() > 0) {
                        for (SubscriptionInfo subscriptionInfo : list) {
                            if (subscriptionInfo.getSubscriptionId() == id) {
                                return String.valueOf(subscriptionInfo.getCarrierName());
                            }
                        }
                        return "UNKNOWN";
                    }
                    return "UNKNOWN";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "UNKNOWN";
                }
            }
            return "UNKNOWN";
        }
        return "UNKNOWN";
    }
}
