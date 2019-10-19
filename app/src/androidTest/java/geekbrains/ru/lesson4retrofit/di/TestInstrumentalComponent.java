package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.DataInstrumentalTest;
import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.di.modules.AppModule;
import geekbrains.ru.lesson4retrofit.di.modules.DataModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;

@Component(modules = {NetworkModule.class, DataModule.class, RetrofitTestModule.class, AppModule.class})
@Singleton
public interface TestInstrumentalComponent {
    void inject(DataInstrumentalTest test);
    void inject(NetworkHelper helper);


    RestAPI getApi();
}
