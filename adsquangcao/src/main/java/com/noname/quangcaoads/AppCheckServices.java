package com.noname.quangcaoads;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.noname.quangcaoads.ads.AdsFullScreen;
import com.noname.quangcaoads.model.ControlAds;
import com.noname.quangcaoads.util.FileCacheUtil;
import com.noname.quangcaoads.util.LogUtils;
import com.noname.quangcaoads.util.SharedPreferencesGlobalUtil;

import net.mready.hover.Hover;
import net.mready.hover.HoverWindow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppCheckServices extends Service {

    public static final String TAG = "AppCheckServices";
    private String myPackage;

    private Gson gson;
    private AdsReceiver receiverAds;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler handler;
    private ArrayList<String> listLauncher;
    private boolean isInitFullScreen, hasPermissionBanner;
    private ControlAds controlAds;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isServiceEnable = true;
        try {
            String str = SharedPreferencesGlobalUtil.getValue(this, "isServiceEnable");
            if (!TextUtils.isEmpty(str)) {
                isServiceEnable = Boolean.valueOf(str);
            }
        } catch (Exception e) {
        }
        if (!isServiceEnable) {
            stopSelf();
            return START_NOT_STICKY;
        }

        LogUtils.logBlue(TAG, "     Service onStartCommand !!!");
        myPackage = getPackageName();
        controlAds = null;
        sendNotify();

        if (receiverAds != null)
            unregisterReceiver(receiverAds);
        IntentFilter filter = new IntentFilter();
        filter.addAction("XXX.AdsReceiver");
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiverAds = new AdsReceiver();
        registerReceiver(receiverAds, filter);

        //////////////////
        listLauncher = new ArrayList<>();
        Intent intentLauncher = new Intent(Intent.ACTION_MAIN);
        intentLauncher.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> lst = getApplicationContext().getPackageManager().queryIntentActivities(intentLauncher, 0);
        if (!lst.isEmpty()) {
            for (ResolveInfo resolveInfo : lst) {
                listLauncher.add(resolveInfo.activityInfo.packageName);
            }
        }

        isInitFullScreen = false;
        firstBanner = false;
        timeCreate = System.currentTimeMillis();

        if (Hover.hasOverlayPermission(this) && isPermissionToReadHistory()) {
            hasPermissionBanner = true;
        } else {
            hasPermissionBanner = false;
        }
        //////////////////

        handler = new Handler();
        QuangCaoSetup.initiate(getApplicationContext(), new QuangCaoSetup.QuangCaoSetupCallback() {
            @Override
            public void onSuccess() {
                try {
                    runStartAds(AppCheckServices.this.getApplicationContext());
                } catch (Exception e) {
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        resetAll();
        if (receiverAds != null)
            unregisterReceiver(receiverAds);
        receiverAds = null;

        LogUtils.logRed(TAG, "     Service onDestroy !!!");
        super.onDestroy();
    }

    private void sendNotify() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    int delay = random.nextInt(1900 - 500 + 1) + 500;
                    Thread.sleep(delay);
                    Intent intentSend = new Intent("com.nnvd.comvsapp");
                    intentSend.putExtra("package", AppCheckServices.this.getPackageName());
                    intentSend.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intentSend);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    private void runStartAds(Context context) {
        try {
            resetAll();
            if (gson == null)
                gson = new Gson();
            String strConfig = FileCacheUtil.loadConfig(context);
            controlAds = gson.fromJson(strConfig, ControlAds.class);
            scheduleShowAds(2, 1);
        } catch (Exception e) {
        }
    }

    private String getPakageCurrent() {
        if (Build.VERSION.SDK_INT < 21) {
            try {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                if (taskInfo != null) {
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    if (componentInfo.getPackageName() != null)
                        return componentInfo.getPackageName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT == 21) {
            try {
                final int PROCESS_STATE_TOP = 2;
                ActivityManager.RunningAppProcessInfo currentInfo = null;
                Field field = null;
                try {
                    field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
                } catch (Exception ignored) {
                }
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo app : appList) {
                    if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            && app.importanceReasonCode == ActivityManager.RunningAppProcessInfo.REASON_UNKNOWN) {
                        Integer state = null;
                        try {
                            state = field.getInt(app);
                        } catch (Exception e) {
                        }
                        if (state != null && state == PROCESS_STATE_TOP) {
                            currentInfo = app;
                            break;
                        }
                    }
                }
                if (currentInfo == null && appList.size() > 1)
                    currentInfo = appList.get(0);
                return currentInfo.processName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= 22) {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            if (!TextUtils.isEmpty(mpackageName) && !mpackageName.equals(myPackage))
                return mpackageName;

            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            final long INTERVAL = 1800000;
            final long end = System.currentTimeMillis();
            final long begin = end - INTERVAL;
            UsageEvents usageEvents = usm.queryEvents(begin, end);
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin, end);
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                mpackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
            while (usageEvents.hasNextEvent()) {
                UsageEvents.Event event = new UsageEvents.Event();
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    String temp = event.getPackageName();
                    if (!TextUtils.isEmpty(temp) && mpackageName.equals(temp))
                        return mpackageName;
                }
            }

            if (!TextUtils.isEmpty(mpackageName))
                return mpackageName;
        }
        return "XXX";
    }

    private void scheduleShowAds(int delay, int period) {
        mTimer = new Timer();
        if (mTimerTask != null)
            mTimerTask.cancel();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                try {
                    if (isScreenOn(AppCheckServices.this) &&
                            UserPresentReceiver.isMyServiceRunning(AppCheckServices.this,
                                    AppCheckServices.class.getName())) {
                        LogUtils.logBlue("X_Time", "Full  " + AdsFullScreen.getTimeConLaiDeShowPopup(getApplicationContext()) +
                                "    ---      Banner  " + getTimeConLaiDeShowBanner());
                        if (Math.min(AdsFullScreen.getTimeConLaiDeShowPopup(getApplicationContext()), getTimeConLaiDeShowBanner()) > 3000) {
                            resetAll();
                            AppCheckServices.this.stopSelf();
                            return;
                        }

                        if (!isInitFullScreen) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        AdsFullScreen.initiate(AppCheckServices.this);
                                    } catch (Exception e) {
                                    }
                                }
                            });
                            isInitFullScreen = true;
                        }

                        if (hasPermissionBanner) {
                            long time_Old = System.currentTimeMillis();
                            String packageNow = getPakageCurrent();
                            long time_New = System.currentTimeMillis();
                            LogUtils.logRed("XXX", (time_New - time_Old) + "   " + packageNow);
                            try {
                                if (listLauncher.contains(packageNow) && isOkBanner()) {
                                    openWindow(1235588, OverlayWindow.class);
                                }
                            } catch (Exception e) {
                            }
                        }

                        try {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AdsFullScreen.showAds(AppCheckServices.this);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        resetAll();
                        AppCheckServices.this.stopSelf();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resetAll();
                    try {
                        AppCheckServices.this.stopSelf();
                    } catch (Exception e1) {
                    }
                }
            }
        };
        mTimer.schedule(mTimerTask, delay * 1000l, period * 1000l);
    }

    private boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = pm.isScreenOn();
        } else {
            isScreenOn = pm.isInteractive();
        }
        return isScreenOn;
    }

    private void resetAll() {
        try {
            if (mTimerTask != null)
                mTimerTask.cancel();
            mTimerTask = null;
        } catch (Exception e) {
        }
        try {
            if (mTimer != null)
                mTimer.cancel();
            mTimer = null;
        } catch (Exception e) {
        }
        try {
            AdsFullScreen.destroy();
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        AdsFullScreen.destroy();
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    private void openWindow(int id, Class<? extends HoverWindow> window) {
        if (Hover.hasOverlayPermission(this)) {
            firstBanner = true;
            timeShowOld = System.currentTimeMillis();
            FileCacheUtil.setTimeShowBannerAds(getApplicationContext());
            Hover.showWindow(this, id, window);
        }
    }

    private boolean firstBanner;
    private long timeShowOld = 0, timeCreate = 0;
    private long timeDelayAds = 0, firstDelayAds = 0;

    private boolean isOkBanner() {
        if (timeDelayAds == 0) {
            timeDelayAds = controlAds.getConfig_show().getOffset_show_banner();
        }
        if (firstDelayAds == 0) {
            firstDelayAds = controlAds.getConfig_show().getTime_start_banner();
        }
        long timeNow = System.currentTimeMillis();
        if (timeShowOld == 0)
            timeShowOld = FileCacheUtil.getTimeShowBannerAds(getApplicationContext());
        if (!firstBanner) {
            if ((timeNow - timeCreate) > (firstDelayAds * 1000)
                    && (timeNow - timeShowOld) > (timeDelayAds * 1000)) {
                return true;
            }
        } else {
            if ((timeNow - timeShowOld) > (timeDelayAds * 1000)) {
                return true;
            }
        }
        return false;
    }

    public int getTimeConLaiDeShowBanner() {
        if (!hasPermissionBanner) {
            return 9999999;
        } else {
            try {
                if (timeDelayAds == 0) {
                    timeDelayAds = controlAds.getConfig_show().getOffset_show_banner();
                }
                if (firstDelayAds == 0) {
                    firstDelayAds = controlAds.getConfig_show().getTime_start_banner();
                }
                try {
                    long timeNow = System.currentTimeMillis();
                    if (timeShowOld == 0)
                        timeShowOld = FileCacheUtil.getTimeShowBannerAds(getApplicationContext());
                    int denta;
                    if (!firstBanner) {
                        denta = (int) Math.max((timeCreate + firstDelayAds * 1000 - timeNow) / 1000,
                                (timeShowOld + timeDelayAds * 1000 - timeNow) / 1000);
                    } else {
                        denta = (int) ((timeShowOld + timeDelayAds * 1000 - timeNow) / 1000);
                    }
                    return denta;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private class AdsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            try {
                if (isScreenOn(context)) {
                    QuangCaoSetup.initiate(context.getApplicationContext(), new QuangCaoSetup.QuangCaoSetupCallback() {
                        @Override
                        public void onSuccess() {
                            try {
                                runStartAds(context.getApplicationContext());
                            } catch (Exception e) {
                            }
                        }
                    });
                } else {
                    resetAll();
                    AppCheckServices.this.stopSelf();
                }
            } catch (Exception e) {
            }
        }
    }

    private boolean isPermissionToReadHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        return false;
    }

}
