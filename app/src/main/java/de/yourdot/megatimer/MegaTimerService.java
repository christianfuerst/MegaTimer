package de.yourdot.megatimer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import de.yourdot.megatimer.model.MegaTimer;

public class MegaTimerService extends Service {

    private static final String TAG = MegaTimerService.class.getCanonicalName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long megatimer_id = intent.getLongExtra("megatimer_id", 0);
        MegaTimer megaTimer = MegaTimer.findById(MegaTimer.class, megatimer_id);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(megaTimer.getColor())
                        .setContentTitle(getString(R.string.service_notification_title))
                        .setContentText(megaTimer.getTitle() + " " +
                                getString(R.string.service_notification_text));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) megatimer_id, builder.build());

        megaTimer.setStatus("finished");
        megaTimer.setStart(megaTimer.getStop());
        megaTimer.save();

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }
}
