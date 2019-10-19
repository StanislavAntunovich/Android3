package geekbrains.ru.lesson4retrofit.di;

import dagger.Subcomponent;

@Subcomponent(modules = {NetworkTestModule.class})
public interface NetworkTestComponent extends NetworkComponent {

    @Override
    boolean isConnected();
}
