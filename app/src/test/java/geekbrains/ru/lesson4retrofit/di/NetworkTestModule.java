package geekbrains.ru.lesson4retrofit.di;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkTestModule {
    @Provides
    boolean isConnected() {
        return true;
    }
}
