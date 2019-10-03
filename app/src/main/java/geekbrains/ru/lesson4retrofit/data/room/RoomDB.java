package geekbrains.ru.lesson4retrofit.data.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;

@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public abstract UserDao getUsersDao();
}
