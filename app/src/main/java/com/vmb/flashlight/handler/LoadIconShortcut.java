package com.vmb.flashlight.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.vmb.flashlight.util.ShortcutUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadIconShortcut extends AsyncTask {

    private Context context;
    private String name;
    private String icon;
    private String url;

    public LoadIconShortcut(Context context, String name, String icon, String url) {
        this.context = context;
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    Bitmap bitmap;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("LoadIconShortcut", "name = " + name);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            //Tiến hành tạo đối tượng URL
            URL urlConnection = new URL(icon);

            //Mở kết nối
            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();

            //Đọc dữ liệu
            InputStream input = connection.getInputStream();

            //Convert
            bitmap = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        ShortcutUtil.addShortcut(context, name, bitmap, url);
    }
}
