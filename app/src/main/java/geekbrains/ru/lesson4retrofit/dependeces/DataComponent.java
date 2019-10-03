package geekbrains.ru.lesson4retrofit.dependeces;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson4retrofit.MainPresenter;

@Singleton
@Component(modules = {NetworkModule.class, DataModule.class, ActivityModule.class})
public interface DataComponent {

    void injectToPresenter(MainPresenter presenter);

    boolean isConnected();
}
