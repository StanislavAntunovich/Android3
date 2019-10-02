package geekbrains.ru.lesson4retrofit.dependeces;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

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
