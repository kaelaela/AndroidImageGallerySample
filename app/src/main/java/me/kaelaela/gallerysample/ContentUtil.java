package me.kaelaela.gallerysample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
        Cursor c = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Thumbnails.IMAGE_ID + " DESC");
        List<String> thumbnailIds = new ArrayList<>();//Get original data by this id.
        List<String> thumbnailPaths = new ArrayList<>();
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String data = c.getString(c.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                String imageId = c.getString(c.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                thumbnailPaths.add(data);
                thumbnailIds.add(imageId);
                c.moveToNext();
            }
            c.close();
        }
        adapter.addAllItem(thumbnailPaths);
    }
}
