package ch.swisso;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class SwissOApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
