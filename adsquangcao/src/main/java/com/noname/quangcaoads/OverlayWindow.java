package com.noname.quangcaoads;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.noname.quangcaoads.ads.AdsBannerView;

import net.mready.hover.HoverWindow;

public class OverlayWindow extends HoverWindow {

    private View btnClose;

    private AdsBannerView adsBannerView;

    @Override
    protected void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        setContentView(R.layout.window_overlay_xxxads, params);

        btnClose = findViewById(R.id.btn_close_xxxads);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    close();
                } catch (Exception e) {
                }
            }
        });

        adsBannerView = findViewById(R.id.ads_banner_view_xxxads);
        adsBannerView.setCallbackBanner(new AdsBannerView.CallbackBanner() {
            @Override
            public void onError() {
                try {
                    close();
                } catch (Exception e) {
                }
            }

            @Override
            public void onLoaded() {
                try {
                    btnClose.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
        });
        adsBannerView.loadAds();
    }

    @Override
    protected void onDestroy() {
        try {
            adsBannerView.destroy();
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}