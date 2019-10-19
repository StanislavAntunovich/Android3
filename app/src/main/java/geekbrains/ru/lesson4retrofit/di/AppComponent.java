package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.MainActivity;
import geekbrains.ru.lesson4retrofit.UserActivity;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.di.modules.AppModule;
import geekbrains.ru.lesson4retrofit.di.modules.DataModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;
import geekbrains.ru.lesson4retrofit.di.modules.PresenterModule;
import geekbrains.ru.lesson4retrofit.di.modules.RetrofitModule;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;

@Singleton
@Component(modules = {AppModule.class, DataModule.class, RetrofitModule.class, PresenterModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(UserActivity activity);

    void inject(MainPresenter presenter);
    void inject(UsersPresenter presenter);
    void inject(NetworkHelper helper);
    void inject(DataHelper helper);

    NetworkComponent getNetworkComponent(NetworkModule module);
}
