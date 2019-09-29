package geekbrains.ru.lesson4retrofit.rest;

import java.util.List;

import geekbrains.ru.lesson4retrofit.data.RepoEntity;
import geekbrains.ru.lesson4retrofit.data.UserEntity;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestAPI {
    @GET("users/{path}")
    Single<UserEntity> getUser(@Path("path") String user);

    @GET("users")
    Single<List<UserEntity>> getAllUsers();

    @GET("users/{path}/repos")
    Single<List<RepoEntity>> getUserRepos(@Path("path") String user);
}
