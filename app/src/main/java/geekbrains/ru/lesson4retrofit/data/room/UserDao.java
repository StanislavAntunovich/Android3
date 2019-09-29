package geekbrains.ru.lesson4retrofit.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import geekbrains.ru.lesson4retrofit.data.UserEntity;

@Dao
public interface UserDao {
    @Query("SELECT * FROM UserEntity")
    List<UserEntity> getAllUsers();

    @Insert
    void saveAll(List<UserEntity> data);

    @Query("DELETE FROM UserEntity")
    void deleteAll();
}
