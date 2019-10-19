package geekbrains.ru.lesson4retrofit;

import android.app.Application;
import android.content.Context;

import geekbrains.ru.lesson4retrofit.di.AppComponent;
import geekbrains.ru.lesson4retrofit.di.DaggerAppComponent;
import geekbrains.ru.lesson4retrofit.di.NetworkComponent;
import geekbrains.ru.lesson4retrofit.di.modules.AppModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;

public class GitHubApp extends Application {
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }

    public static NetworkComponent getNetworkComponent(Context context) {
        return component.getNetworkComponent(new NetworkModule(context));
    }

}
