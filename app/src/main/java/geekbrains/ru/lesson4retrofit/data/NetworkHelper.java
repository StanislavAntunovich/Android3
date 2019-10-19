package geekbrains.ru.lesson4retrofit.data;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NetworkHelper {
    private List<UserEntity> currentUsers;
    private List<RepoEntity> currentRepos;
    @Inject
    RestAPI api;

    public NetworkHelper(AppComponent component) {
        currentUsers = new ArrayList<>();
        currentRepos = new ArrayList<>();
        component.inject(this);
    }

    public Single<List<RepoEntity>> getRepos(String userName) {
        return api.getUserRepos(userName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Single<UserEntity> singleUser(String name) {
        return api.getUser(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<UserEntity>> getAllUsers() {
        return api.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<UserEntity>> loadUser(String name) {
        return Single.create(emitter -> {
            singleUser(name).subscribe(result -> {
                List<UserEntity> list = new ArrayList<>(1);
                list.add(result);
                emitter.onSuccess(list);
            });
        });
    }

    public void setCurrentRepos(List<RepoEntity> repos) {
        this.currentRepos = repos;
    }

    public void setCurrentUsers(List<UserEntity> users) {
        this.currentUsers = users;
    }

    public List<UserEntity> getCurrentUsers() {
        return currentUsers;
    }

    public List<RepoEntity> getCurrentRepos() {
        return currentRepos;
    }
}
