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
    public DataHelper getDataHelper() {
        return Mockito.mock(DataHelper.class);
    }

    @Provides
    @Singleton
    public NetworkHelper getNetworkHelper() {
        return Mockito.mock(NetworkHelper.class);
    }

    @Provides
    @Singleton
    AppComponent getAppComponent() {
        AppComponent component = Mockito.mock(AppComponent.class);
        return component;
    }
}
