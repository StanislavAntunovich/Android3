package geekbrains.ru.lesson4retrofit.di;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.data.MainDataHelper;
import geekbrains.ru.lesson4retrofit.data.UsersDataHelper;

@Module
public class TestModule {

    @Provides
    @Singleton
    public MainDataHelper getModel() {
        return Mockito.mock(MainDataHelper.class);
    }

    @Provides
    @Singleton
    public UsersDataHelper getUserHelper() {
        return Mockito.mock(UsersDataHelper.class);
    }
}
