package geekbrains.ru.lesson4retrofit.rest;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestApi {
    @GET("users/{path}")
    Single<RetrofitUserModel> getUser(@Path("path") String user);

    @GET("users")
    Single<List<RetrofitUserModel>> getAllUsers();

    @GET("users/{path}/repos")
    Single<List<RetrofitRepoModel>> getUserRepos(@Path("path") String user);
}
