package com.vmb.flashlight.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.noname.quangcaoads.QuangCaoSetup;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.Interface.IAPIControl;
import com.vmb.flashlight.Interface.IGetCountry;
import com.vmb.flashlight.adapter.ItemAdapter;
import com.vmb.flashlight.base.BaseActivity;
import com.vmb.flashlight.handler.FlashModeHandler;
import com.vmb.flashlight.model.Ads;
import com.vmb.flashlight.model.Flashlight;
import com.vmb.flashlight.receiver.ConnectionReceiver;
import com.vmb.flashlight.util.AdUtil;
import com.vmb.flashlight.util.CountryCodeUtil;
import com.vmb.flashlight.util.DeviceUtil;
import com.vmb.flashlight.util.NetworkUtil;
import com.vmb.flashlight.util.RetrofitInitiator;
import com.vmb.flashlight.util.SharedPreferencesUtil;
import com.vmb.flashlight.util.TimeRegUtil;
import com.vmb.flashlight.util.ToastUtil;
import com.vmb.touchclick.listener.OnTouchClickListener;

import java.util.ArrayList;
import java.util.List;

import flashlight.supper.flashlight.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements IGetCountry {

    private int row_lenght = 29;

    private ImageView img_switch;
    private ImageView img_setting;
    private FrameLayout container;
    private TextView lbl_indicator_light;

    private int indicator = 0;

    @Override
    protected int getResLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        img_switch = findViewById(R.id.imb_switch);
        img_setting = findViewById(R.id.img_setting);
        container = findViewById(R.id.container);
        lbl_indicator_light = findViewById(R.id.lbl_indicator_light);
    }

    public void addShortcut() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=chemhoaqua.chemtraicay.chemca.chemchuoi"));

        Intent extra = new Intent();
        extra.putExtra("android.intent.extra.shortcut.INTENT", intent);
        extra.putExtra("android.intent.extra.shortcut.NAME", "My app");
        extra.putExtra("android.intent.extra.shortcut.ICON", Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        extra.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(extra);
    }

    @Override
    protected void initData() {
        addShortcut();

        img_setting.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        }, getApplicationContext(), 2));

        boolean sound = SharedPreferencesUtil.getPrefferBool(getApplicationContext(), "sound", true);
        Flashlight.getInstance().setSound(sound);
        ConnectionReceiver.getInstance().setActivity(MainActivity.this);

        QuangCaoSetup.initiate(MainActivity.this);
        initGetAds();

        if (!isFlashSupported()) {
            showNoFlashAlert();
        }
        else {
            Camera camera = Camera.open();
            Flashlight.getInstance().setCamera(camera);
            Flashlight.getInstance().setParameters(camera.getParameters());
            initRecyclerView();
            setupBehavior();
        }
    }

    public void initGetAds() {
        if (NetworkUtil.isNetworkAvailable(getApplicationContext()))
            CountryCodeUtil.getCountryCode(this);
    }

    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void showNoFlashAlert() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.not_support)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    public void initRecyclerView() {
        List<String> listData = new ArrayList<>();
        for (int i = 0; i < this.row_lenght; i++)
            listData.add("");

        final RecyclerView recycler = findViewById(R.id.recycler);

        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);

        // Setting the LayoutManager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);

        // Setting the adapter.
        ItemAdapter adapter = new ItemAdapter(MainActivity.this, listData);
        recycler.setAdapter(adapter);

        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                indicator = recyclerView.computeHorizontalScrollOffset() / recyclerView.getChildAt(0).getMeasuredWidth();
                lbl_indicator_light.setText(indicator + "");

                if (Flashlight.getInstance().isFlashLightOn())
                    FlashModeHandler.getInstance().setMode(indicator);
            }
        });
    }

    public void setupBehavior() {
        final PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
        final PointF StartPT = new PointF(); // Record Start Position of 'img'

        final int limitY_top = 0;

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                final int container_height = container.getHeight();
                final int limitY_bottom = limitY_top + container_height;

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    container.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                Resources r = getResources();
                final int view_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, r.getDisplayMetrics());
                final int x = (int) img_switch.getX();

                img_switch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                int testY = (int) (StartPT.y + event.getY() - DownPT.y);

                                if (testY < 0) {
                                    img_switch.setY(limitY_top);
                                    break;
                                }

                                if (testY + view_height > limitY_bottom) {
                                    img_switch.setY(limitY_bottom - view_height);
                                    break;
                                }

                                img_switch.setY(testY);
                                StartPT.set(x, testY);

                                if ((testY - limitY_top + view_height / 2) > (container_height / 2)) {
                                    if (Flashlight.getInstance().isFlashLightOn() == false)
                                        break;

                                    // Turn off flashlight
                                    Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_OFF);
                                    Flashlight.getInstance().setFlashLightOn(false);
                                    img_switch.setImageResource(R.drawable.img_switch_off);
                                    Flashlight.getInstance().playToggleSound(getApplicationContext());

                                    AdUtil.getInstance().displayInterstitial();

                                } else {
                                    if (Flashlight.getInstance().isFlashLightOn() == true)
                                        break;

                                    // Turn on flashlight
                                    Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_TORCH);
                                    Flashlight.getInstance().setFlashLightOn(true);
                                    img_switch.setImageResource(R.drawable.img_switch_on);
                                    Flashlight.getInstance().playToggleSound(getApplicationContext());

                                    FlashModeHandler.getInstance().setMode(indicator);
                                    Flashlight.getInstance().getCamera().startPreview();
                                }
                                break;

                            case MotionEvent.ACTION_DOWN:
                                DownPT.set(event.getX(), event.getY());
                                StartPT.set(img_switch.getX(), img_switch.getY());
                                break;

                            case MotionEvent.ACTION_UP:
                                int Y = (int) img_switch.getY();
                                if ((Y - limitY_top + view_height / 2) <= (container_height / 2)) {
                                    // Set switch to ON mode position
                                    img_switch.setY(limitY_top);

                                } else {
                                    // Set switch to OFF mode position
                                    img_switch.setY(limitY_bottom - view_height);
                                }
                                break;

                            default:
                                break;
                        }

                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onGetCountry(String country) {
        final String TAG = "initGetAds";

        final String deviceID = DeviceUtil.getDeviceId(getApplicationContext());
        Log.d(TAG, "deviceID = " + deviceID);
        final String code = Config.CODE_CONTROL_APP;
        Log.d(TAG, "code = " + code);
        final String version = Config.VERSION_APP;
        Log.d(TAG, "version = " + version);
        final String timereg = TimeRegUtil.getTimeRegister(getApplicationContext());
        Log.d(TAG, "timereg = " + timereg);
        final String packg = Config.PACKAGE_NAME;
        Log.d(TAG, "packg = " + packg);

        IAPIControl api = RetrofitInitiator.createService(IAPIControl.class, Config.Url.URL_BASE);
        Call<Ads> call = api.getAds(deviceID, code, version, country, timereg, packg);
        call.enqueue(new Callback<Ads>() {
            @Override
            public void onResponse(Call<Ads> call, Response<Ads> response) {
                Log.d(TAG, "onResponse()");
                if (response == null)
                    return;

                if (response.isSuccessful()) {
                    Log.d(TAG, "response.isSuccessful()");

                    if (response.body() == null || isFinishing()) {
                        Log.d(TAG, "response.body() null || activity.isFinishing()");
                        return;
                    }

                    Ads.setInstance(response.body());
                    if (response.body().getShow_ads() == 0)
                        return;

                    loadBannerAd();
                    AdUtil.getInstance().initInterstitialAd(getApplicationContext());
                    AdUtil.getInstance().initCountDown();
                    AdUtil.getInstance().setInitGetAds(true);
                } else {
                    Log.d(TAG, "response.failed");
                }
            }

            @Override
            public void onFailure(Call<Ads> call, Throwable t) {
                Log.d(TAG, "onFailure()");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (Flashlight.getInstance().getCamera() != null) {
            Flashlight.getInstance().getCamera().stopPreview();
            Flashlight.getInstance().getCamera().release();
            Flashlight.getInstance().setCamera(null);
        }

        AdUtil.getInstance().cancelDownCount();
        AdUtil.getInstance().setInitGetAds(false);
        AdUtil.getInstance().setShowPopupFirstTime(false);
        super.onDestroy();
    }
}
