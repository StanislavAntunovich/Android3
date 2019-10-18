package geekbrains.ru.lesson4retrofit.di.modules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {
    private final Context activity;

    public NetworkModule(Context activity) {
        this.activity = activity;
    }

    @Provides
    boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager != null ? manager.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }
}
