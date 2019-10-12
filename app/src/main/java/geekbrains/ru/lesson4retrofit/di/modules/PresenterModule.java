package geekbrains.ru.lesson4retrofit.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.GitHubApp;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;

@Module
public class PresenterModule {

    @Provides
    @Singleton
    MainPresenter getMainPresenter() {
        return new MainPresenter(GitHubApp.getComponent());
    }

    @Provides
    @Singleton
    UsersPresenter getUsersPresenter() {
        return new UsersPresenter(GitHubApp.getComponent());
    }

}
