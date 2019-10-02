package geekbrains.ru.lesson4retrofit.dependeces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.MainActivity;

@Module
public class ActivityModule {
    private Context context;
    private MainActivity activity;

    public ActivityModule(MainActivity activity) {
        this.context = activity;
        this.activity = activity;
    }

    @Provides
    public Context getActivityContext() {
        return this.context;
    }

    @Provides
    public MainActivity getActivity() {
        return this.activity;
    }

    @Provides
    public ConnectivityManager getConnectManager(MainActivity activity) {
        return (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    public NetworkInfo getNetInfo(ConnectivityManager manager) {
        return manager != null ? manager.getActiveNetworkInfo() :
                null;
    }

    @Provides
    public boolean isConnected(NetworkInfo networkInfo) {
        return !(networkInfo != null && networkInfo.isConnected());
    }
}
