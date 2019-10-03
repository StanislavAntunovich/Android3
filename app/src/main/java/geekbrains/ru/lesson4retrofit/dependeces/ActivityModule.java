package geekbrains.ru.lesson4retrofit.dependeces;

import android.content.Context;

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

}
