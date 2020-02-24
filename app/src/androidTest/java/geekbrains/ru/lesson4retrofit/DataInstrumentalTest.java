package geekbrains.ru.lesson4retrofit;

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

import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.DaggerTestInstrumentalComponent;
import geekbrains.ru.lesson4retrofit.di.RetrofitTestModule;
import geekbrains.ru.lesson4retrofit.di.TestInstrumentalComponent;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static junit.framework.TestCase.assertEquals;

public class DataInstrumentalTest {
    private static List<UserEntity> USERS_LIST;
    private static UserEntity USER;

    @Inject
    NetworkHelper networkHelper;

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
                .retrofitTestModule(new RetrofitTestModule() {
                    @Override
                    public String baseUrl() {
                        return webServer.url("/").toString();
                    }
                })
                .build();

        component.inject(this);
        component.inject(networkHelper);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        webServer.shutdown();
    }

    @Test
    public void getOneUserTest() {
        webServer.enqueue(getResponse(USER, 200));
        TestObserver<List<UserEntity>> observer = TestObserver.create();
        networkHelper.loadUser(USER.getLogin()).subscribe(observer);
        observer.awaitTerminalEvent();
        observer.assertValueCount(1);
        assertEquals(USER.getLogin(), observer.values().get(0).get(0).getLogin());
    }

    @Test
    public void getAllUsersTest() {
        webServer.enqueue(getResponse(USERS_LIST, 200));
        TestObserver<List<UserEntity>> observer = new TestObserver<>();
        networkHelper.getAllUsers().subscribe(observer);
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

        networkHelper.getAllUsers().subscribe(observer);
        observer.awaitTerminalEvent();
        observer.assertError(HttpException.class);
    }


    private MockResponse getResponse(Object bodyObj, int code) {
        String body = new Gson().toJson(bodyObj);
        return new MockResponse().setBody(body).setResponseCode(code);
    }
}
