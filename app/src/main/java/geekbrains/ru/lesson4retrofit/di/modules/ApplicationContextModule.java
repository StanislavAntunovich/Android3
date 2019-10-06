package geekbrains.ru.lesson4retrofit.di.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.di.qualifiers.ApplicationContext;

@Module
public class ApplicationContextModule {
    private Context context;

    public ApplicationContextModule(Context context) {
        this.context = context;
    }

    @ApplicationContext
    @Provides
    public Context getAppContext() {
        return context.getApplicationContext();
    }
}
