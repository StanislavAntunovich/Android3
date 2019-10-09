package geekbrains.ru.lesson4retrofit.di.modules;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.MainActivity;

@Module
public class ActivityModule {
    private Context context;
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.context = activity;
        this.activity = activity;
    }

    @Provides
    public Context getActivityContext() {
        return this.context;
    }

    @Provides
    public MainActivity getMainActivity() {
        return (MainActivity) this.activity;
    }

}
