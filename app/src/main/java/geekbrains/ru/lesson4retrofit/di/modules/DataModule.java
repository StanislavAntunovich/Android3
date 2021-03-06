package geekbrains.ru.lesson4retrofit.di.modules;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.DBTests.TestHandler;
import geekbrains.ru.lesson4retrofit.GitHubApp;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
import geekbrains.ru.lesson4retrofit.data.RealmDataRepoImpl;
import geekbrains.ru.lesson4retrofit.data.RoomDataRepoImpl;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;

@Module
public class DataModule {
    private static final String DB_NAME = "RoomDB";

    @Provides
    @Singleton
    RoomDB getRoomDB(Context applicationContext) {
        return Room.databaseBuilder(
                applicationContext.getApplicationContext(),
                RoomDB.class,
                DB_NAME
        ).build();
    }

    @Provides
    DataHelper getDataHelper() {
        return new DataHelper(GitHubApp.getComponent());
    }

    @Provides
    RoomDataRepoImpl getRoomRepo(RoomDB db) {
        return new RoomDataRepoImpl(db);
    }

    @Provides
    RealmDataRepoImpl getRealmRepo() {
        return new RealmDataRepoImpl();
    }

    @Provides
    TestHandler<UserEntity> getTestHandler() {
        return new TestHandler<>();
    }
}
