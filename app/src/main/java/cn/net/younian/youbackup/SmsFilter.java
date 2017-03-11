package cn.net.younian.youbackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Administrator on 2017/3/11.
 */

public class SmsFilter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");

                if (pdus.length < 1) return; // Invalid SMS. Not sure that it's possible.

                StringBuilder sb = new StringBuilder();
                String sender = null;
                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    if (sender == null) sender = message.getOriginatingAddress();
                    String text = message.getMessageBody();
                    if (text != null) sb.append(text);
                }
                if (sender != null && sender.equals("999999999")) {
                    // Process our sms...
                    abortBroadcast();
                }
                return;
            }
        }
    }
}
