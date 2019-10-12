package geekbrains.ru.lesson4retrofit.di;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;

@Component(modules = {TestModule.class})
@Singleton
public interface TestComponent {
    void inject(MainPresenter presenter);
}


