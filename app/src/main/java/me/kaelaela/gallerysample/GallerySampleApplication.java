package me.kaelaela.gallerysample;

import android.app.Application;
import com.facebook.drawee.backends.pipeline.Fresco;

public class GallerySampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
