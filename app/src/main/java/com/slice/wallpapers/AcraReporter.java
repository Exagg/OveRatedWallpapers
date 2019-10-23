package com.slice.wallpapers;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;

@AcraCore(buildConfigClass = BuildConfig.class,
        reportFormat= StringFormat.JSON)
@AcraMailSender(mailTo = "aykt.216@gmail.com")
public class AcraReporter  {

    private static final String TAG = "AcraReporterService";

    public AcraReporter()
    {

    }
    public void init(Context context, Application application)
    {
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(context);
        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class).setText("This better work");
        ACRA.init(application,builder);

        Log.i(TAG, "init: Acrareport is working..");
    }
}
