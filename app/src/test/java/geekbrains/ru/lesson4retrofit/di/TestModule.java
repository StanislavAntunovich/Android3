package geekbrains.ru.lesson4retrofit.di;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
import geekbrains.ru.lesson4retrofit.data.NetworkHelper;

@Module
public class TestModule {

    @Provides
    @Singleton
    public DataHelper getModel() {
        return Mockito.mock(DataHelper.class);
    }

    @Provides
    @Singleton
    public NetworkHelper getUserHelper() {
        return Mockito.mock(NetworkHelper.class);
    }
}
