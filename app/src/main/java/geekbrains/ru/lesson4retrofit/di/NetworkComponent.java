package geekbrains.ru.lesson4retrofit.di;

import dagger.Subcomponent;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;

@Subcomponent(modules = NetworkModule.class)
public interface NetworkComponent {
    boolean isConnected();
}
