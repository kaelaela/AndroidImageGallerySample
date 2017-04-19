package me.kaelaela.gallerysample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ContentUtil {
    public static void setImageList(Context context, AbstractListAdapter<String> adapter) {
        ContentResolver resolver = context.getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ((AppCompatActivity) context).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }
        }
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        List<String> urls = new ArrayList<>();
        while (!cursor.isLast()) {
            Log.d("TAG", " uri:" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            urls.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            cursor.moveToNext();
        }
        cursor.close();
        adapter.addAllItem(urls);
    }
}
