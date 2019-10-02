package geekbrains.ru.lesson4retrofit.dependeces;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ActivityModule.class)
public class RetrofitModule {

    @Provides
    public Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public RestAPI getRestApi(Retrofit retrofit) {
        return retrofit.create(RestAPI.class);
    }




}
