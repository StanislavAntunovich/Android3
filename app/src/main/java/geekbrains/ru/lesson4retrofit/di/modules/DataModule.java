package geekbrains.ru.lesson4retrofit.di.modules;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.GitHubApp;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
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
}
