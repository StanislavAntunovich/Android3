package geekbrains.ru.lesson4retrofit;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.MainDataHelper;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.DaggerTestComponent;
import geekbrains.ru.lesson4retrofit.di.TestComponent;
import geekbrains.ru.lesson4retrofit.di.TestModule;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MainPresenterTest {
    private static List<UserEntity> USERS_LIST;
    private static final double SPEED_RESULT = 2.5;
    private static UserEntity USER;

    @Inject
    MainDataHelper model;

    private MainPresenter presenter;

    @Mock
    private MainActivity view;

    @BeforeClass
    public static void setupClass() {
        USERS_LIST = new ArrayList<>();
        USERS_LIST.add(new UserEntity(1, "1", "1url", "1url"));
        USERS_LIST.add(new UserEntity(2, "2", "2url", "2url"));
        USERS_LIST.add(new UserEntity(3, "3", "3url", "3url"));

        USER = new UserEntity();
        USER.setUrl("someUrl");
        USER.setLogin("someLogin");
        USER.setId(1);
        USER.setAvatarUrl("avatarUrl");
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        presenter = new MainPresenter();
        TestComponent component = DaggerTestComponent.builder()
                .testModule(new TestModule() {
                    @Override
                    public MainDataHelper getModel() {
                        MainDataHelper model = super.getModel();
                        Mockito.when(model.subscribeOnResults())
                                .thenReturn(Observable.just(SPEED_RESULT));
                        Mockito.when(model.testRealmLoadData())
                                .thenReturn(Single.just(USERS_LIST));
                        Mockito.when(model.testRoomLoadData())
                                .thenReturn(Single.just(USERS_LIST));

                        Mockito.when(model.getAllUsers())
                                .thenReturn(Single.just(USERS_LIST));

                        Mockito.when(model.loadUser(USER.getLogin()))
                                .thenReturn(Single.just(USER));
                        return model;
                    }
                })
                .build();

        component.inject(presenter);
        component.inject(this);

        presenter.bindView(view);

    }

    @After
    public void tearDown() {
        presenter.unbindView();
    }

    @Test
    public void bindViewTest() {
        Mockito.verify(model).subscribeOnResults();
    }


    @Test
    public void onRealmLoadTest() {
        presenter.onRealmLoadClicked();
        Mockito.verify(view).startProgress();
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
        Mockito.verify(view).setResult(SPEED_RESULT);
        Mockito.verify(view).updateResult();
        Mockito.verify(view).updateRecyclerData(USERS_LIST);
        Mockito.verify(view).updateRecyclerView();

        Mockito.verify(model).testRealmLoadData();
    }

    @Test
    public void onRoomLoadTest() {
        presenter.onRoomLoadClicked();
        Mockito.verify(view).startProgress();
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
        Mockito.verify(view).setResult(SPEED_RESULT);
        Mockito.verify(view).updateResult();
        Mockito.verify(view).updateRecyclerData(USERS_LIST);
        Mockito.verify(view).updateRecyclerView();

        Mockito.verify(model).testRoomLoadData();
    }

    @Test
    public void onRealmSaveTest() {
        presenter.onRealmSaveClicked(USERS_LIST);
        Mockito.verify(view).startProgress();
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
        Mockito.verify(view).setResult(SPEED_RESULT);
        Mockito.verify(view).updateResult();

        Mockito.verify(model).testRealmSaveData(USERS_LIST);
    }

    @Test
    public void onRoomSaveTest() {
        presenter.onRoomSaveClicked(USERS_LIST);
        Mockito.verify(view).startProgress();
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
        Mockito.verify(view).setResult(SPEED_RESULT);
        Mockito.verify(view).updateResult();

        Mockito.verify(model).testRoomSaveData(USERS_LIST);
    }

    @Test
    public void loadAllUsersTest() {
        presenter.loadNetData("");
        Mockito.verify(model).getAllUsers();
        Mockito.verify(view).updateRecyclerData(USERS_LIST);
        Mockito.verify(view).updateRecyclerView();
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
    }

    @Test
    public void loadSingleUserTest() {
        presenter.loadNetData(USER.getLogin());
        Mockito.verify(view).startProgress();
        Mockito.verify(model).loadUser(USER.getLogin());
        Mockito.verify(view).updateRecyclerData(Arrays.asList(USER));
        Mockito.verify(view).updateRecyclerView();
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
    }


    @Test
    public void loadFailureTest() {
        String error = "user not found";
        HttpException e = new HttpException(
                Response.error(404,
                        ResponseBody.create(MediaType.parse("application/json"),
                                error))
        );
        Mockito.when(model.getAllUsers()).thenReturn(Single.create(emitter -> emitter.onError(e)));
        presenter.loadNetData("");
        Mockito.verify(view, Mockito.atLeastOnce()).stopProgress();
        Mockito.verify(view).showError(e.getLocalizedMessage());
    }

}
