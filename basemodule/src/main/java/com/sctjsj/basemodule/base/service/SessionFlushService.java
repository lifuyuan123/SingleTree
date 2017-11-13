package com.sctjsj.basemodule.base.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sctjsj.basemodule.base.receiver.AlarmReceiver;


/**
 * Created by Chris-Jason on 2016/10/28.
 */

/**
 * 刷新session定时任务
 */
public class SessionFlushService extends Service {
    private String TAG="SessionFlushService";


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO
        //在这里做定时任务的逻辑



        send();
        return super.onStartCommand(intent, flags, startId);
    }



    private void send(){
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        //10分钟发送一次广播
        int tenMin=1*1000*60*10;
        long triggerAtTime= SystemClock.elapsedRealtime()+tenMin;
        Intent i=new Intent(SessionFlushService.this,AlarmReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(SessionFlushService.this,0,i,0);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }





}
