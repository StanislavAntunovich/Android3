package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.DataInstrumentedTest;
import geekbrains.ru.lesson4retrofit.UserDataInstrumentedTest;
import geekbrains.ru.lesson4retrofit.data.MainDataHelper;
import geekbrains.ru.lesson4retrofit.di.modules.ActivityModule;
import geekbrains.ru.lesson4retrofit.di.modules.DataModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;

@Component(modules = {NetworkModule.class, DataModule.class, ActivityModule.class})
@Singleton
public interface TestInstrumentalComponent {
    void inject(DataInstrumentedTest test);
    void inject(MainDataHelper dataWorker);
    void inject(UserDataInstrumentedTest test);

    RestAPI getApi();
}
