package de.yourdot.megatimer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MegaTimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, MegaTimerService.class);
        service_intent.putExtra("megatimer_id", intent.getLongExtra("megatimer_id", 0));
        context.startService(service_intent);
    }
}
