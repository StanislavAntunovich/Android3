package geekbrains.ru.lesson4retrofit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import geekbrains.ru.lesson4retrofit.di.DaggerTestComponent;
import geekbrains.ru.lesson4retrofit.di.TestComponent;
import geekbrains.ru.lesson4retrofit.di.TestModule;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;
import io.reactivex.Single;
import io.reactivex.observers.DisposableObserver;

public class UserPresenterTest {
    private static List<RepoEntity> REPO_LIST;
    @Spy
    private DisposableObserver<Boolean> progress;
    @Spy
    private DisposableObserver<List<RepoEntity>> data;

    @Inject
    NetworkHelper networkHelper;

    private UsersPresenter presenter;

    @Mock
    private UserActivity view;

    @BeforeClass
    public static void setupClass() {
        REPO_LIST = new ArrayList<>();
        REPO_LIST.add(new RepoEntity(1L, "name1", false, "Java", "url1"));
        REPO_LIST.add(new RepoEntity(2L, "name2", false, "Java", "url2"));
        REPO_LIST.add(new RepoEntity(3L, "name3", false, "Java", "url3"));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TestComponent component = getTestComponent();
        component.inject(this);

        AppComponent appComponent = component.getAppComponent();
        presenter = Mockito.spy(new UsersPresenter(appComponent));
        component.inject(presenter);
        presenter.bindView(progress, data, REPO_LIST.get(0).getName());
    }

    private TestComponent getTestComponent() {
        return DaggerTestComponent.builder()
                .testModule(new TestModule() {

                    @Override
                    public NetworkHelper getNetworkHelper() {
                        NetworkHelper networkHelper = super.getNetworkHelper();
                        Mockito.when(networkHelper.getCurrentRepos())
                                .thenReturn(REPO_LIST);
                        Mockito.when(networkHelper.getRepos(Mockito.anyString()))
                                .thenReturn(Single.just(REPO_LIST));
                        return networkHelper;
                    }
                })
                .build();
    }

    @After
    public void tearDown() {
        presenter.unbindView();
    }

    @Test
    public void bindTest() {
        Mockito.verify(networkHelper, Mockito.atLeastOnce()).getRepos(Mockito.anyString());
        Mockito.verify(data, Mockito.atLeastOnce()).onNext(REPO_LIST);
        Mockito.verify(progress).onNext(true);
        Mockito.verify(progress).onNext(false);
        Mockito.verify(networkHelper).setCurrentRepos(REPO_LIST);
    }
}
