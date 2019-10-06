package geekbrains.ru.lesson4retrofit.di.modules;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekbrains.ru.lesson4retrofit.data.DataWorker;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;
import geekbrains.ru.lesson4retrofit.di.qualifiers.ApplicationContext;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;

@Module(includes = ApplicationContextModule.class)
public class DataModule {
    private static final String DB_NAME = "RoomDB";

    @Provides
    public DataWorker getDataHelper(RoomDB db, RestAPI api) {
        return new DataWorker(db, api);
    }

    @Provides
    @Singleton
    public RoomDB getRoomDB(@ApplicationContext Context applicationContext) {
        return Room.databaseBuilder(
                applicationContext.getApplicationContext(),
                RoomDB.class,
                DB_NAME
        ).build();
    }
}
