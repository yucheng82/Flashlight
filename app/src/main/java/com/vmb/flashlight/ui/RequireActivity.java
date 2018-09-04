package com.vmb.flashlight.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vmb.flashlight.util.LanguageUtil;

import flashlight.supper.flashlight.R;

public class RequireActivity extends AppCompatActivity {

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        load();
    }

    public void load() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        String content = "";
        String title = "";

        if (LanguageUtil.isVietnamese()) {
            if (bundle.containsKey("update_message_vn"))
                content = bundle.getString("update_message_vn");
            if (TextUtils.isEmpty(content))
                content = "Đã có phiên bản mới, bạn vui lòng cập nhật!";

            if (bundle.containsKey("update_title_vn"))
                title = bundle.getString("update_title_vn");
            if (TextUtils.isEmpty(content))
                title = "Thông báo cập nhật";
        } else {
            if (bundle.containsKey("update_message_en"))
                content = bundle.getString("update_message_en");
            if (TextUtils.isEmpty(content))
                content = "There is a new version, please update soon!";

            if (bundle.containsKey("update_title_en"))
                title = bundle.getString("update_title_en");
            if (TextUtils.isEmpty(content))
                title = "Update";
        }

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.layout_dialog, null);

        TextView lbl_title = alertLayout.findViewById(R.id.lbl_title);
        lbl_title.setText(title);
        TextView lbl_content = alertLayout.findViewById(R.id.lbl_content);
        lbl_content.setText(content);

        AlertDialog.Builder alert = new AlertDialog.Builder(RequireActivity.this);
        alert.setView(alertLayout);
        alertDialog = alert.create();

        alertLayout.findViewById(R.id.img_close).setVisibility(View.GONE);

        alertLayout.findViewById(R.id.btn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                if (bundle.containsKey("update_url"))
                    url = bundle.getString("update_url");
                if (TextUtils.isEmpty(url))
                    url = "https://play.google.com/store/apps/developer?id=Fruit+Game+Studio";

                alertDialog.cancel();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivityForResult(intent, 1);
            }
        });

        alertDialog = alert.create();
        alertDialog.show();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            recreate();
        }
    }
}
