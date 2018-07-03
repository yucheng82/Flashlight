package com.vmb.flashlight.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PointF;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vmb.flashlight.R;
import com.vmb.flashlight.Static;
import com.vmb.flashlight.adapter.ItemAdapter;
import com.vmb.flashlight.base.BaseActivity;
import com.vmb.flashlight.handler.FlashModeHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ImageButton btn_switch;
    private FrameLayout container;
    private TextView lbl_indicator_light;

    private int indicator = 0;

    @Override
    protected int getResLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Find view
        btn_switch = findViewById(R.id.btn_switch);
        container = findViewById(R.id.container);
        lbl_indicator_light = findViewById(R.id.lbl_indicator_light);
    }

    @Override
    protected void initData() {
        if (!isFlashSupported()) {
            showNoFlashAlert();
        }
        else {
            Static.camera = Camera.open();
            Static.parameters = Static.camera.getParameters();
            initRecyclerView();
            setupBehavior();
        }
    }

    @Override
    protected void onDestroy() {
        if (Static.camera != null) {
            Static.camera.stopPreview();
            Static.camera.release();
            Static.camera = null;
        }
        super.onDestroy();
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
        for (int i = 0; i < 18; i++)
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

                if (Static.isFlashLightOn)
                    FlashModeHandler.setMode(indicator);
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
                final int x = (int) btn_switch.getX();

                btn_switch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                int testY = (int) (StartPT.y + event.getY() - DownPT.y);

                                if (testY < 0)
                                    break;

                                if (testY + view_height > limitY_bottom)
                                    break;

                                btn_switch.setY(testY);
                                StartPT.set(x, testY);

                                if ((testY - limitY_top + view_height / 2) > (container_height / 2)) {
                                    if (!Static.isFlashLightOn)
                                        break;

                                    // Turn off flashlight
                                    Static.parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    Static.camera.setParameters(Static.parameters);
                                    //Config.camera.stopPreview();
                                    Static.isFlashLightOn = false;

                                } else {
                                    if (Static.isFlashLightOn)
                                        break;

                                    // Turn on flashlight
                                    Static.parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    Static.camera.setParameters(Static.parameters);
                                    Static.camera.startPreview();
                                    Static.isFlashLightOn = true;

                                    FlashModeHandler.setMode(indicator);
                                }
                                break;

                            case MotionEvent.ACTION_DOWN:
                                DownPT.set(event.getX(), event.getY());
                                StartPT.set(btn_switch.getX(), btn_switch.getY());
                                break;

                            case MotionEvent.ACTION_UP:
                                int Y = (int) btn_switch.getY();
                                if ((Y - limitY_top + view_height / 2) <= (container_height / 2)) {
                                    // Set switch to ON mode position
                                    btn_switch.setY(limitY_top);
                                    btn_switch.setImageResource(R.drawable.panel_led_switch_on_19);
                                } else {
                                    // Set switch to OFF mode position
                                    btn_switch.setY(limitY_bottom - view_height);
                                    btn_switch.setImageResource(R.drawable.panel_led_switch_19);
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
}
