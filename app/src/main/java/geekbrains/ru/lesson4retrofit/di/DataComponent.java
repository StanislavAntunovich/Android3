package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.di.modules.ActivityModule;
import geekbrains.ru.lesson4retrofit.di.modules.DataModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;

@Singleton
@Component(modules = {NetworkModule.class, DataModule.class, ActivityModule.class})
public interface DataComponent {

    void injectToPresenter(MainPresenter presenter);
    void injectToUserPresenter(UsersPresenter presenter);

    boolean isConnected();
}
