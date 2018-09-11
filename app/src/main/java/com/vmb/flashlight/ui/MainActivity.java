package com.vmb.flashlight.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noname.quangcaoads.QuangCaoSetup;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.Interface.IAPIControl;
import com.vmb.flashlight.Interface.IGetCountry;
import com.vmb.flashlight.adapter.ItemAdapter;
import com.vmb.flashlight.handler.FlashModeHandler;
import com.vmb.flashlight.model.Ads;
import com.vmb.flashlight.model.Flashlight;
import com.vmb.flashlight.receiver.ConnectionReceiver;
import com.vmb.flashlight.util.AdmobUtil;
import com.vmb.flashlight.util.AdsUtil;
import com.vmb.flashlight.util.CountryCodeUtil;
import com.vmb.flashlight.util.DeviceUtil;
import com.vmb.flashlight.util.FBAdsUtil;
import com.vmb.flashlight.util.NetworkUtil;
import com.vmb.flashlight.util.PermissionUtils;
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

public class MainActivity extends Activity implements IGetCountry {

    private int row_lenght = 40;

    private FrameLayout layout_ads;
    private RelativeLayout banner;

    private ImageView img_switch;
    private ImageView img_setting;
    private FrameLayout container;
    private TextView lbl_indicator_light;

    private int indicator = 0;
    private int countBack = 0;

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());
        initView();
        initData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA, Manifest.permission.CAMERA);
            } else {
                setupCamera();
            }
        } else {
            setupCamera();
        }
    }

    protected int getResLayout() {
        return R.layout.activity_main;
    }

    protected void initView() {
        img_switch = findViewById(R.id.imb_switch);
        img_setting = findViewById(R.id.img_setting);
        container = findViewById(R.id.container);
        lbl_indicator_light = findViewById(R.id.lbl_indicator_light);
    }

    protected void initData() {
        if (!isFlashSupported()) {
            showNoFlashAlert();
            return;
        }
        layout_ads = findViewById(R.id.layout_ads);
        banner = findViewById(R.id.banner);
        initGetAds();

        setupBehavior();
        initRecyclerView();

        img_setting.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                countBack = 0;
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        }, getApplicationContext(), 2));

        boolean sound = SharedPreferencesUtil.getPrefferBool(getApplicationContext(), "sound", true);
        Flashlight.getInstance().setSound(sound);
        ConnectionReceiver.getInstance().setActivity(MainActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                QuangCaoSetup.initiate(MainActivity.this);
            }
        }).run();
    }

    public void setupCamera() {
        if (Flashlight.getInstance().getCamera() == null) {
            Camera camera = null;
            try {
                camera = Camera.open();
            } catch (Exception e) {
                ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));
                return;
            }
            if (camera == null) {
                ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));
                return;
            }

            Flashlight.getInstance().setCamera(camera);
            Flashlight.getInstance().setParameters(camera.getParameters());
        }
    }

    public void initGetAds() {
        if (NetworkUtil.isNetworkAvailable(getApplicationContext()))
            CountryCodeUtil.getCountryCode(this);
    }

    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        if (pm != null)
            return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        else
            return false;
    }

    private void showNoFlashAlert() {
        ToastUtil.longToast(getApplicationContext(), getString(R.string.not_support));
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
                countBack = 0;

                int check = indicator;
                indicator = recyclerView.computeHorizontalScrollOffset() / recyclerView.getChildAt(0).getMeasuredWidth();
                lbl_indicator_light.setText(indicator + "");

                if (check != indicator) {
                    Flashlight.getInstance().playMoveSound(getApplicationContext());

                    if (indicator == 0 || indicator == 20)
                        Flashlight.getInstance().playEndSound(getApplicationContext());
                }

                if (Flashlight.getInstance().isFlashLightOn())
                    FlashModeHandler.getInstance().setMode(MainActivity.this, indicator);
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

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    container.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final int view_height = img_switch.getHeight();
                final int x = (int) img_switch.getX();

                img_switch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (isFinishing())
                            return false;

                        countBack = 0;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                int testY = (int) (StartPT.y + event.getY() - DownPT.y);

                                if (testY < limitY_top) {
                                    img_switch.setY(limitY_top);
                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AdsUtil.getInstance().displayInterstitial();
                                                }
                                            }).run();
                                        }
                                    }, 1000);
                                    break;
                                }

                                if (testY + view_height > limitY_bottom) {
                                    img_switch.setY(limitY_bottom - view_height);
                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AdsUtil.getInstance().displayInterstitial();
                                                }
                                            }).run();
                                        }
                                    }, 1000);
                                    break;
                                }

                                img_switch.setY(testY);
                                StartPT.set(x, testY);

                                if ((testY - limitY_top + view_height / 2) > (container_height / 2)) {
                                    if (Flashlight.getInstance().isFlashLightOn() == false)
                                        break;

                                    // Turn off flashlight
                                    Flashlight.getInstance().setFlashLightOn(false);
                                    Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_OFF);

                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    img_switch.setImageResource(R.drawable.img_switch_off);
                                                }
                                            }).run();
                                        }
                                    }, 50);

                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Flashlight.getInstance().playToggleSound(getApplicationContext());
                                                }
                                            }).run();
                                        }
                                    }, 100);

                                } else {
                                    if (Flashlight.getInstance().isFlashLightOn() == true)
                                        break;

                                    // Turn on flashlight
                                    Flashlight.getInstance().setFlashLightOn(true);
                                    FlashModeHandler.getInstance().setMode(MainActivity.this, indicator);

                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    img_switch.setImageResource(R.drawable.img_switch_on);
                                                }
                                            }).run();
                                        }
                                    }, 50);

                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Flashlight.getInstance().playToggleSound(getApplicationContext());
                                                }
                                            }).run();
                                        }
                                    }, 100);
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

                boolean on = Flashlight.getInstance().isFlashLightOn();
                if (on) {
                    img_switch.setImageResource(R.drawable.img_switch_on);
                    img_switch.setY(limitY_top);
                }
            }
        });
    }

    @Override
    public void onGetCountry(final String country) {
        final String TAG = "onGetCountry";
        if (isFinishing()) {
            Log.d(TAG, "activity.isFinishing()");
            return;
        }

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

                    if (Ads.getInstance().getUpdate_status() != 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("update_title_vn", Ads.getInstance().getUpdate_title_vn());
                        bundle.putString("update_title_en", Ads.getInstance().getUpdate_title_en());
                        bundle.putString("update_message_vn", Ads.getInstance().getUpdate_message_vn());
                        bundle.putString("update_message_en", Ads.getInstance().getUpdate_message_en());
                        bundle.putString("update_url", Ads.getInstance().getUpdate_url());

                        if (Ads.getInstance().getUpdate_status() == 1) {
                            intent = new Intent(MainActivity.this, SplashActivity.class);
                            intent.putExtras(bundle);
                        } else if (Ads.getInstance().getUpdate_status() == 2) {
                            intent = new Intent(MainActivity.this, RequireActivity.class);
                            intent.putExtras(bundle);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (intent != null)
                                    startActivity(intent);
                            }
                        });
                    }

                    AdsUtil.getInstance().initCountDown();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Ads.getInstance().getAds_network().equals("facebook")) {
                                AdmobUtil.getInstance().initBannerAdmob(getApplicationContext(), banner, layout_ads);
                            } else {
                                FBAdsUtil.getInstance().initBannerFB(getApplicationContext(), banner, layout_ads);
                            }
                        }
                    }, 1000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FBAdsUtil.getInstance().initInterstitialFB(MainActivity.this);
                        }
                    }, 5000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AdmobUtil.getInstance().initInterstitialAdmob(MainActivity.this);
                        }
                    }, 7000);

                    AdsUtil.getInstance().setInitGetAds(true);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.warning_request_permission));
                } else {
                    setupCamera();
                }
                break;

            case Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA_IN_SET_MODE:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.warning_request_permission));
                } else {
                    setupCamera();
                    FlashModeHandler.getInstance().setMode(MainActivity.this, indicator);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        /*if (Flashlight.getInstance().getCamera() != null) {
            Flashlight.getInstance().getCamera().stopPreview();
            Flashlight.getInstance().getCamera().release();
            Flashlight.getInstance().setInstance(null);
        }*/

        AdsUtil.getInstance().cancelDownCount();
        AdsUtil.getInstance().setInstance(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.w("onKeyBack", "onKeyBack()");
        countBack++;
        if (countBack == 1)
            Toast.makeText(getApplicationContext(), R.string.press_back_again, Toast.LENGTH_LONG).show();
        else if (countBack == 2) {
            AdsUtil.getInstance().setShowPopupCloseApp(true);
            AdsUtil.getInstance().displayInterstitial();
        } else
            finish();
    }
}
