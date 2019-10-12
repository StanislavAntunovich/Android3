package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
import geekbrains.ru.lesson4retrofit.di.modules.DataModule;
import geekbrains.ru.lesson4retrofit.di.modules.RetrofitModule;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;

@Component(modules = {RetrofitModule.class, DataModule.class, ActivityModule.class})
@Singleton
public interface TestInstrumentalComponent {
    void inject(DataInstrumentedTest test);
    void inject(DataHelper dataWorker);
    void inject(UserDataInstrumentedTest test);

    RestAPI getApi();
}
