package geekbrains.ru.lesson4retrofit.di;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.data.DataWorker;

@Module
public class TestModule {

    @Provides
    @Singleton
    public DataWorker getModel() {
        return Mockito.mock(DataWorker.class);
    }
}
