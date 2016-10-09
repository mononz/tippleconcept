package net.mononz.tipple;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.mononz.tipple.sync.NetworkInterface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Tipple extends Application {

    private static String LOG_TAG = Tipple.class.getSimpleName();

    private static GoogleAnalytics analytics;
    private static Tracker mTracker;
    private static String ANALYTICS_KEY;

    public static NetworkInterface network;

    public final OkHttpClient client = new OkHttpClient();

    @Override
    public void onCreate() {
        super.onCreate();

        ANALYTICS_KEY = getString(R.string.analytics_tracking);
        analytics = GoogleAnalytics.getInstance(this);
        FirebaseAnalytics.getInstance(this);

        getSession(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        OkHttpClient newClient = client.newBuilder()
                .readTimeout(getResources().getInteger(R.integer.network_time_out), TimeUnit.SECONDS)
                .connectTimeout(getResources().getInteger(R.integer.network_time_out), TimeUnit.SECONDS)
                .writeTimeout(getResources().getInteger(R.integer.network_time_out), TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request.Builder builder = originalRequest.newBuilder();
                        builder.addHeader(BuildConfig.AUTH_HEADER, BuildConfig.BASIC_AUTH);
                        originalRequest = builder.build();
                        Response response = chain.proceed(originalRequest);
                        if (BuildConfig.DEBUG) {
                            String bodyString = response.body().string();
                            Log.d(LOG_TAG, String.format("%s | %s | %s | %s", originalRequest.url(), response.code(), originalRequest.headers().toString().replaceAll("\n", ""), bodyString));
                            response = response.newBuilder().body(ResponseBody.create(response.body().contentType(), bodyString)).build();
                        }
                        return response;
                    }
                })
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(newClient)
                .build();

        network = restAdapter.create(NetworkInterface.class);

        mTracker = getDefaultTracker();
        if (mTracker != null) {
            Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                    getDefaultTracker(),                         // Currently used Tracker.
                    Thread.getDefaultUncaughtExceptionHandler(), // Current default uncaught exception handler.
                    this);                                       // Context of the application.
            // Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(myHandler);
        }
    }

    public static PreferenceManager getSession(Context context) {
        return new PreferenceManager(context);
    }

    synchronized private static Tracker getDefaultTracker() {
        try {
            if (mTracker == null) {
                mTracker = analytics.newTracker(ANALYTICS_KEY);
                mTracker.enableAdvertisingIdCollection(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTracker;
    }

    public static void sendScreen(String screen) {
        Log.d("Analytics (Screen)", screen);
        if (!BuildConfig.DEBUG) {
            Tracker mTracker = getDefaultTracker();
            if (mTracker != null) {
                mTracker.setScreenName(screen);
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        }
    }

    public static void sendAction(String key, String action) {
        Log.d("Analytics (Action)", key + " " + action);
        if (!BuildConfig.DEBUG) {
            Tracker mTracker = getDefaultTracker();
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(key)
                        .setAction(action)
                        .build());
            }
        }
    }

    public static void sendAction(String key, String action, String label) {
        Log.d("Analytics (Action)", key + " " + action);
        if (!BuildConfig.DEBUG) {
            Tracker mTracker = getDefaultTracker();
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(key)
                        .setAction(action)
                        .setLabel(label)
                        .build());
            }
        }
    }

    public boolean timeForSync() {
        double threshold_millis  = BuildConfig.SYNC_HOURS * 60 * 60 * 1000;
        return System.currentTimeMillis() > (getSession(this).getLastUpdated() + threshold_millis);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}