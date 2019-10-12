package geekbrains.ru.lesson4retrofit.di.modules;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.GitHubApp;
import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    @Provides
    String baseUrl() {
        return "https://api.github.com/";
    }

    @Provides
    Retrofit getRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RestAPI getRestApi(Retrofit retrofit) {
        return retrofit.create(RestAPI.class);
    }

    @Provides
    NetworkHelper getNetworkHelper() {
        return new NetworkHelper(GitHubApp.getComponent());
    }

}
