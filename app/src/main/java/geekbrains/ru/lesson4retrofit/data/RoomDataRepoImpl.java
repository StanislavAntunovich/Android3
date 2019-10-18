package geekbrains.ru.lesson4retrofit.data;

import java.util.List;

import geekbrains.ru.lesson4retrofit.DBTests.DataRepo;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;

public class RoomDataRepoImpl implements DataRepo<UserEntity> {

    private RoomDB roomDB;

    public RoomDataRepoImpl(RoomDB roomDB) {
        this.roomDB = roomDB;
    }

    @Override
    public List<UserEntity> getAll() {
        return roomDB.getUsersDao().getAllUsers();
    }

    @Override
    public void saveAll(List<UserEntity> data) {
        roomDB.getUsersDao().saveAll(data);
    }

    @Override
    public void deleteAll() {
        roomDB.getUsersDao().deleteAll();
    }
}
