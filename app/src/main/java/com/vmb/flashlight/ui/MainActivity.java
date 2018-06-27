package com.vmb.flashlight.ui;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.vmb.flashlight.R;
import com.vmb.flashlight.adapter.ItemAdapter;
import com.vmb.flashlight.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ImageButton btn_switch;
    private FrameLayout container;

    private Camera.Parameters parameters;
    private Camera camera;

    private boolean isFlashLightOn;

    @Override
    protected int getResLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Remove title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Find view
        btn_switch = findViewById(R.id.btn_switch);
        container = findViewById(R.id.container);
    }

    @Override
    protected void initData() {
        if (!isFlashSupported())
            showNoFlashAlert();
        else {
            camera = Camera.open();
            parameters = camera.getParameters();
        }

        initRecyclerView();
        setupBehavior();
    }

    @Override
    protected void onDestroy() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }

    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void showNoFlashAlert() {
        new AlertDialog.Builder(this)
                .setMessage("Your device hardware does not support flashlight!")
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Error")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    public void initRecyclerView() {
        List<String> listData = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            listData.add("");

        RecyclerView recyclerView = findViewById(R.id.recycler);

        // If the size of views will not change as the data changes.
        recyclerView.setHasFixedSize(true);

        // Setting the LayoutManager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Setting the adapter.
        ItemAdapter adapter = new ItemAdapter(MainActivity.this, listData);
        recyclerView.setAdapter(adapter);
    }

    public void setupBehavior() {
        final PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
        final PointF StartPT = new PointF(); // Record Start Position of 'img'

        final int limitY_top = (int) container.getY();
        final int container_height = container.getLayoutParams().height;
        final int limitY_bottom = limitY_top + container_height;

        final int view_height = btn_switch.getLayoutParams().height;
        final int x = (int) btn_switch.getX();

        btn_switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int testY = (int) (StartPT.y + event.getY() - DownPT.y);

                        if (testY < limitY_top)
                            break;

                        if (testY + view_height > limitY_bottom)
                            break;

                        btn_switch.setY(testY);
                        StartPT.set(x, testY);

                        if ((testY - limitY_top + view_height / 2) > (container_height / 2)) {
                            if (!isFlashLightOn)
                                break;

                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(parameters);
                            //Config.camera.stopPreview();
                            isFlashLightOn = false;

                        } else {
                            if (isFlashLightOn)
                                break;

                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(parameters);
                            camera.startPreview();
                            isFlashLightOn = true;
                        }

                        break;

                    case MotionEvent.ACTION_DOWN:
                        DownPT.set(event.getX(), event.getY());
                        StartPT.set(btn_switch.getX(), btn_switch.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        // Nothing have to do
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }
}
