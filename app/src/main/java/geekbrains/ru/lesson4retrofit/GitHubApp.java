package geekbrains.ru.lesson4retrofit;

import android.app.Application;

import geekbrains.ru.lesson4retrofit.di.AppComponent;
import geekbrains.ru.lesson4retrofit.di.DaggerAppComponent;
import geekbrains.ru.lesson4retrofit.di.modules.AppModule;

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

}
