package geekbrains.ru.lesson4retrofit;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.UsersDataHelper;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.DaggerTestInstrumentalComponent;
import geekbrains.ru.lesson4retrofit.di.modules.ApplicationContextModule;
import geekbrains.ru.lesson4retrofit.di.modules.NetworkModule;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static junit.framework.TestCase.assertEquals;

public class UserDataInstrumentedTest {
    private static List<RepoEntity> REPOS;
    private static UserEntity USER;

    private static MockWebServer mockWebServer;


    @Inject
    UsersDataHelper helper;


    @BeforeClass
    public static void setupClass() throws IOException {
        USER = new UserEntity(1, "mojombo", "avatar url", "url");

        REPOS = new ArrayList<>();
        REPOS.add(new RepoEntity(1L, "1 repo", false, "Java", "url"));
        REPOS.add(new RepoEntity(2L, "2 repo", false, "Java", "url"));
        REPOS.add(new RepoEntity(3L, "3 repo", false, "Java", "url"));

        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        mockWebServer.shutdown();
    }

    @Before
    public void setup() {
        DaggerTestInstrumentalComponent.builder()
                .networkModule(new NetworkModule() {
                    @Override
                    public String baseUrl() {
                        return mockWebServer.url("/").toString();
                    }
                })
                .applicationContextModule(new ApplicationContextModule(InstrumentationRegistry.getInstrumentation().getTargetContext()))
                .build()
                .inject(this);

    }

    @After
    public void tearDown() {
        helper = null;
    }

    @Test
    public void getReposTest() {
        mockWebServer.enqueue(getResponse(REPOS, 200));
        TestObserver<List<RepoEntity>> observer = TestObserver.create();
        helper.getRepos(USER.getLogin()).subscribe(observer);
        observer.awaitTerminalEvent();

        assertEquals(REPOS.size(), observer.values().get(0).size());
        assertEquals(REPOS.get(0).getName(), observer.values().get(0).get(0).getName());
        assertEquals(REPOS.get(1).getName(), observer.values().get(0).get(1).getName());
        assertEquals(REPOS.get(2).getName(), observer.values().get(0).get(2).getName());
    }

    @Test
    public void getReposFailureTest() {
        mockWebServer.enqueue(getResponse(null, 404));
        TestObserver<List<RepoEntity>> observer = TestObserver.create();
        helper.getRepos(USER.getLogin()).subscribe(observer);
        observer.awaitTerminalEvent();
        observer.assertError(HttpException.class);
    }

    private MockResponse getResponse(Object bodyObj, int code) {
        String body = new Gson().toJson(bodyObj);
        return new MockResponse().setBody(body).setResponseCode(code);
    }

}
