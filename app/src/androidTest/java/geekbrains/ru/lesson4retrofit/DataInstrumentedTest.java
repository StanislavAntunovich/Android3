package geekbrains.ru.lesson4retrofit;


import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.MainDataHelper;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.DaggerTestInstrumentalComponent;
import geekbrains.ru.lesson4retrofit.di.TestInstrumentalComponent;
import geekbrains.ru.lesson4retrofit.di.modules.ApplicationContextModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.TestObserver;
import io.realm.Realm;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static junit.framework.TestCase.assertEquals;

public class DataInstrumentedTest {
    private static List<UserEntity> USERS_LIST;
    private static UserEntity USER;


    @Inject
    MainDataHelper model;

    private static MockWebServer webServer;

    @BeforeClass
    public static void setupClass() throws IOException {
        webServer = new MockWebServer();
        webServer.start();

        USERS_LIST = new ArrayList<>();
        USERS_LIST.add(new UserEntity(1, "1", "1url", "1url"));
        USERS_LIST.add(new UserEntity(2, "2", "2url", "2url"));
        USERS_LIST.add(new UserEntity(3, "3", "3url", "3url"));

        USER = new UserEntity(42, "someLogin", "avatarUrl", "url");
    }

    @Before
    public void setup() {

        TestInstrumentalComponent component = DaggerTestInstrumentalComponent.builder()
                .networkModule(new NetworkModule() {
                    @Override
                    public String baseUrl() {
                        return webServer.url("/").toString();
                    }
                })
                .applicationContextModule(new ApplicationContextModule(InstrumentationRegistry.getInstrumentation().getTargetContext()))
                .build();

        component.inject(this);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        webServer.shutdown();
    }

    @Test
    public void getSingleUserTest() {
        webServer.enqueue(getResponse(USER, 200));
        TestObserver<UserEntity> observer = TestObserver.create();
        model.loadUser(USER.getLogin()).subscribe(observer);
        observer.awaitTerminalEvent();
        observer.assertValueCount(1);
        assertEquals(USER.getLogin(), observer.values().get(0).getLogin());
    }

    @Test
    public void getAllUsersTest() {
        webServer.enqueue(getResponse(USERS_LIST, 200));
        TestObserver<List<UserEntity>> observer = new TestObserver<>();
        model.getAllUsers().subscribe(observer);
        observer.awaitTerminalEvent();

        assertEquals(USERS_LIST.size(), observer.values().get(0).size());
        assertEquals(USERS_LIST.get(0).getLogin(), observer.values().get(0).get(0).getLogin());
        assertEquals(USERS_LIST.get(1).getLogin(), observer.values().get(0).get(1).getLogin());
        assertEquals(USERS_LIST.get(2).getLogin(), observer.values().get(0).get(2).getLogin());
    }

    @Test
    public void userLoadFailureTest() {
        webServer.enqueue(getResponse(null, 404));
        TestObserver<List<UserEntity>> observer = new TestObserver<>();

        model.getAllUsers().subscribe(observer);
        observer.awaitTerminalEvent();
        observer.assertError(HttpException.class);
    }

    @Test
    public void roomSaveTest() {
        TestObserver<Double> observer = TestObserver.create();
        final List<Double> resultsList = new ArrayList<>();

        model.subscribeOnResults().subscribe(getObserver(observer, resultsList));
        model.testRoomSaveData(USERS_LIST);

        observer.awaitTerminalEvent();
        observer.assertComplete();
        assertEquals(resultsList.get(0), observer.values().get(0));
    }

    @Test
    public void realmSaveTest() {
        Realm.init(InstrumentationRegistry.getInstrumentation().getTargetContext());
        TestObserver<Double> observer = TestObserver.create();
        final List<Double> resultsList = new ArrayList<>();
        DisposableObserver<Double> subj = getObserver(observer, resultsList);

        model.subscribeOnResults().subscribe(subj);
        model.testRealmSaveData(USERS_LIST);

        observer.awaitTerminalEvent();
        observer.assertComplete();
        assertEquals(resultsList, observer.values());
    }

    @Test
    public void roomLoadDataTest() throws InterruptedException {
        model.testRoomSaveData(USERS_LIST);
        Thread.sleep(100);
        TestObserver<List<UserEntity>> entityObserver = TestObserver.create();
        TestObserver<Double> timeObserver = TestObserver.create();
        final List<Double> resultsList = new ArrayList<>();

        model.subscribeOnResults().subscribe(getObserver(timeObserver, resultsList));
        model.testRoomLoadData().subscribe(entityObserver);

        entityObserver.awaitTerminalEvent();
        timeObserver.awaitTerminalEvent();

        timeObserver.assertComplete();
        entityObserver.assertComplete();

        timeObserver.assertValueCount(1);
        assertEquals(USERS_LIST.size(), entityObserver.values().get(0).size());
        assertEquals(USERS_LIST.get(0).getLogin(), entityObserver.values().get(0).get(0).getLogin());
        assertEquals(USERS_LIST.get(1).getLogin(), entityObserver.values().get(0).get(1).getLogin());
        assertEquals(USERS_LIST.get(2).getLogin(), entityObserver.values().get(0).get(2).getLogin());
    }

    @Test
    public void realmLoadDataTest() throws InterruptedException {
        Realm.init(InstrumentationRegistry.getInstrumentation().getTargetContext());
        model.testRealmSaveData(USERS_LIST);
        Thread.sleep(100);

        TestObserver<List<UserEntity>> entityObserver = TestObserver.create();
        TestObserver<Double> timeObserver = TestObserver.create();
        final List<Double> resultsList = new ArrayList<>();

        model.subscribeOnResults().subscribe(getObserver(timeObserver, resultsList));
        model.testRealmLoadData().subscribe(entityObserver);

        entityObserver.awaitTerminalEvent();
        timeObserver.awaitTerminalEvent();

        timeObserver.assertComplete();
        entityObserver.assertComplete();

        timeObserver.assertValueCount(1);
        assertEquals(USERS_LIST.size(), entityObserver.values().get(0).size());
        assertEquals(USERS_LIST.get(0).getLogin(), entityObserver.values().get(0).get(0).getLogin());
        assertEquals(USERS_LIST.get(1).getLogin(), entityObserver.values().get(0).get(1).getLogin());
        assertEquals(USERS_LIST.get(2).getLogin(), entityObserver.values().get(0).get(2).getLogin());
    }

    private <T> DisposableObserver<T> getObserver(TestObserver<T> testObserver, List<T> results) {
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T res) {
                results.add(res);
                testObserver.onNext(res);
                testObserver.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                testObserver.onError(e);
            }

            @Override
            public void onComplete() {
                testObserver.onComplete();
            }
        };
    }


    private MockResponse getResponse(Object bodyObj, int code) {
        String body = new Gson().toJson(bodyObj);
        return new MockResponse().setBody(body).setResponseCode(code);
    }
}
