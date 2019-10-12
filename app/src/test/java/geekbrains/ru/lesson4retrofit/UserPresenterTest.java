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
import java.util.List;

import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static junit.framework.TestCase.assertNotNull;

public class UserPresenterTest {
    private static List<RepoEntity> REPOS;
    private static UserEntity USER;

    @Mock
    private UserActivity view;

    NetworkHelper helper;

    private UsersPresenter presenter;

    @BeforeClass
    public static void setupClass() {
        USER = new UserEntity(1, "mojombo", "avatar url", "url");

        REPOS = new ArrayList<>();
        REPOS.add(new RepoEntity(1L, "1 repo", false, "Java", "url"));
        REPOS.add(new RepoEntity(2L, "2 repo", false, "Java", "url"));
        REPOS.add(new RepoEntity(3L, "3 repo", false, "Java", "url"));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        presenter = new UsersPresenter();


        presenter.bindView(view);
    }

    @After
    public void tearDown() {
        presenter.unbindView();
        presenter = null;
        view = null;
        helper = null;
    }

    @Test
    public void bindViewTest() {
        assertNotNull(view);
    }

    @Test
    public void onRepoClickTest() {
        presenter.onRepoClick(REPOS.get(0));
        Mockito.verify(view).startRepoActivity(REPOS.get(0));
        Mockito.verifyNoMoreInteractions(view);
    }

    @Test
    public void getReposTest() {
        presenter.getRepos(USER.getLogin());
        Mockito.verify(view).startLoading();
        Mockito.verify(helper).getRepos(USER.getLogin());
        Mockito.verify(view).stopLoading();
        Mockito.verify(view).updateData(REPOS);
        Mockito.verifyNoMoreInteractions(view);
        Mockito.verifyNoMoreInteractions(helper);
    }

    @Test
    public void loadFailureTest() {
        String error = "repos not found";
        HttpException e = new HttpException(
                Response.error(404,
                        ResponseBody.create(MediaType.parse("application/json"),
                                error))
        );
        Mockito.when(helper.getRepos(USER.getLogin()))
                .thenReturn(Single.create(emitter -> emitter.onError(e)));

        presenter.getRepos(USER.getLogin());

        Mockito.verify(view).startLoading();
        Mockito.verify(view).stopLoading();
        Mockito.verify(view).showError(e.getLocalizedMessage());
        Mockito.verifyNoMoreInteractions(view);
    }
}
