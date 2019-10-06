package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.MainPresenter;
import geekbrains.ru.lesson4retrofit.di.modules.ActivityModule;
import geekbrains.ru.lesson4retrofit.di.modules.DataModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;

@Singleton
@Component(modules = {NetworkModule.class, DataModule.class, ActivityModule.class})
public interface DataComponent {

    void injectToPresenter(MainPresenter presenter);

    boolean isConnected();
}
