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
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.DBTests.TestResult;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import geekbrains.ru.lesson4retrofit.di.DaggerTestComponent;
import geekbrains.ru.lesson4retrofit.di.NetworkComponent;
import geekbrains.ru.lesson4retrofit.di.TestComponent;
import geekbrains.ru.lesson4retrofit.di.TestModule;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

public class MainPresenterTest {
    private static List<UserEntity> USERS_LIST;
    private static final double SPEED_RESULT = 2.5;
    private static UserEntity USER;
    private static TestResult testResult;
    private PublishSubject<TestResult> resultObservable;
    @Spy
    private DisposableObserver<Boolean> progress;
    @Spy
    private DisposableObserver<TestResult> result;
    @Spy
    private DisposableObserver<List<UserEntity>> data;

    @Inject
    DataHelper dataHelper;
    @Inject
    NetworkHelper networkHelper;

    private MainPresenter presenter;

    @Mock
    private MainActivity view;
    @Mock
    private NetworkComponent networkComponent;

    @BeforeClass
    public static void setupClass() {
        USERS_LIST = new ArrayList<>();
        USERS_LIST.add(new UserEntity(1, "1", "1url", "1url"));
        USERS_LIST.add(new UserEntity(2, "2", "2url", "2url"));
        USERS_LIST.add(new UserEntity(3, "3", "3url", "3url"));

        USER = new UserEntity(1, "someLogin", "avatarUrl", "url");
        testResult = new TestResult(SPEED_RESULT, USERS_LIST.size());
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        resultObservable = PublishSubject.create();

        TestComponent component = getTestComponent();
        component.inject(this);

        networkComponent = component.getNetworkComponent();

        AppComponent appComponent = component.getAppComponent();
        presenter = Mockito.spy(new MainPresenter(appComponent));
        component.inject(presenter);
        presenter.bindView(progress, result, data, networkComponent);
    }

    private TestComponent getTestComponent() {
        return DaggerTestComponent.builder()
                    .testModule(new TestModule() {
                        @Override
                        public DataHelper getDataHelper() {
                            DataHelper dataHelper = super.getDataHelper();
                            Mockito.when(dataHelper.subscribeOnResults())
                                    .thenReturn(resultObservable);
                            Mockito.when(dataHelper.testRealmLoadData())
                                    .thenReturn(Single.just(USERS_LIST));
                            Mockito.when(dataHelper.testRoomLoadData())
                                    .thenReturn(Single.just(USERS_LIST));
                            Mockito.when(dataHelper.testRealmSaveData(USERS_LIST))
                                    .thenReturn(Completable.create(
                                            emitter -> {
                                                resultObservable.onNext(testResult);
                                                emitter.onComplete();
                                            }));
                            Mockito.when(dataHelper.testRoomSaveData(USERS_LIST))
                                    .thenReturn(Completable.create(
                                            emitter -> {
                                                resultObservable.onNext(testResult);
                                                emitter.onComplete();
                                            }));
                            return dataHelper;
                        }

                        @Override
                        public NetworkHelper getNetworkHelper() {
                            NetworkHelper networkHelper = super.getNetworkHelper();
                            Mockito.when(networkHelper.getAllUsers())
                                    .thenReturn(Single.just(USERS_LIST));
                            Mockito.when(networkHelper.loadUser(Mockito.anyString()))
                                    .thenReturn(Single.just(Arrays.asList(USER)));
                            Mockito.when(networkHelper.getCurrentUsers())
                                    .thenReturn(USERS_LIST);
                            return networkHelper;
                        }
                    })
                    .build();
    }

    @After
    public void tearDown() {
        presenter.unBindView();
    }

    @Test
    public void bindTest() {
        Mockito.verify(presenter).bindView(progress, result, data, networkComponent);
        Mockito.verify(data).onNext(USERS_LIST);
    }

    @Test
    public void loadNetAllTest() {
        presenter.loadNetData("");
        Mockito.verify(networkHelper).getAllUsers();
        Mockito.verify(progress).onNext(true);
        Mockito.verify(progress).onNext(false);
        Mockito.verify(networkHelper).setCurrentUsers(Mockito.anyList());
        Mockito.verify(data, Mockito.times(2)).onNext(USERS_LIST);
    }

    @Test
    public void loadNetOneTest() {
        presenter.loadNetData(USER.getLogin());
        Mockito.verify(networkHelper).loadUser(USER.getLogin());
        Mockito.verify(progress).onNext(true);
        Mockito.verify(progress).onNext(false);
        Mockito.verify(networkHelper).setCurrentUsers(Mockito.anyList());
        Mockito.verify(data, Mockito.times(2)).onNext(Mockito.anyList());
    }

    @Test
    public void saveRoomTest() {
        presenter.saveAllRoom();
        Mockito.verify(networkHelper, Mockito.atLeastOnce()).getCurrentUsers();
        Mockito.verify(dataHelper).testRoomSaveData(Mockito.anyList());
    }

    @Test
    public void saveRealmTest() {
        presenter.saveAllRealm();
        Mockito.verify(networkHelper, Mockito.atLeastOnce()).getCurrentUsers();
        Mockito.verify(dataHelper).testRealmSaveData(Mockito.anyList());
    }

    @Test
    public void loadRoomTest() {
        presenter.loadAllRoom();
        Mockito.verify(dataHelper).testRoomLoadData();
    }

    @Test
    public void loadRealmTest() {
        presenter.loadAllRealm();
        Mockito.verify(dataHelper).testRealmLoadData();
    }

}
