package geekbrains.ru.lesson4retrofit.data;

import java.util.List;

import geekbrains.ru.lesson4retrofit.DBTests.DataRepo;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import io.realm.Realm;

public class RealmDataRepoImpl implements DataRepo<UserEntity> {
    @Override
    public List<UserEntity> getAll() {
        Realm db = Realm.getDefaultInstance();
        List<UserEntity> all = db.where(UserEntity.class).findAll();
        return db.copyFromRealm(all);
    }

    @Override
    public void saveAll(List<UserEntity> data) {
        Realm db = Realm.getDefaultInstance();
        db.beginTransaction();
        db.insert(data);
        db.commitTransaction();
    }

    @Override
    public void deleteAll() {
        Realm db = Realm.getDefaultInstance();
        db.beginTransaction();
        db.deleteAll();
        db.commitTransaction();
    }
}
