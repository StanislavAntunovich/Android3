package geekbrains.ru.lesson4retrofit.data;

import java.util.List;

import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UsersDataHelper {
    private RestAPI api;

    public UsersDataHelper(RestAPI api) {
        this.api = api;
    }

    public Single<List<RepoEntity>> getRepos(String userName) {
        return api.getUserRepos(userName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
