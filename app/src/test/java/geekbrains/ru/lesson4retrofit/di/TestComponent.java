package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.MainPresenterTest;
import geekbrains.ru.lesson4retrofit.UserPresenterTest;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;

@Component(modules = {TestModule.class})
@Singleton
public interface TestComponent {
    void inject(MainPresenter presenter);
    void inject(UsersPresenter presenter);

    void inject(MainPresenterTest test);
    void inject(UserPresenterTest test);

    AppComponent getAppComponent();
    NetworkTestComponent getNetworkComponent();
}


