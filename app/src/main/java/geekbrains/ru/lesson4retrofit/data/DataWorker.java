package geekbrains.ru.lesson4retrofit.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;

public class DataWorker {
    private static final int TEST_COUNT = 100;
    private RoomDB roomDB;
    private RestAPI api;

    private PublishSubject<Double> resultsSubject = PublishSubject.create();

    public DataWorker(RoomDB roomDB, RestAPI api) {
        this.roomDB = roomDB;
        this.api = api;
    }

    public Single<UserEntity> loadUser(String name) {
        return api.getUser(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<UserEntity>> getAllUsers() {
        return api.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void testRoomSaveData(List<UserEntity> data) {
        Single<Double> single = Single.create(emitter -> {
            long sum = 0;
            for (int i = 0; i < TEST_COUNT; i++) {
                roomDB.getUsersDao().deleteAll();
                Date date1 = new Date();
                roomDB.getUsersDao().saveAll(data);
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            double result = sum / (double) TEST_COUNT;
            emitter.onSuccess(result);
        });
        single
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<Double>() {
                    @Override
                    public void onSuccess(Double aDouble) {
                        resultsSubject.onNext(aDouble);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public Single<List<UserEntity>> testRoomLoadData() {

        return Single.create((SingleEmitter<List<UserEntity>> emitter) -> {

            List<UserEntity> data = new ArrayList<>();
            long sum = 0;
            for (int i = 0; i < TEST_COUNT; i++) {
                Date date1 = new Date();
                data = roomDB.getUsersDao().getAllUsers();
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }

            double result = sum / (double) TEST_COUNT;
            emitter.onSuccess(data);
            resultsSubject.onNext(result);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void testRealmSaveData(List<UserEntity> data) {
        Single<Double> single = Single.create(emitter -> {
            long sum = 0;
            Realm db = Realm.getDefaultInstance();
            for (int i = 0; i < TEST_COUNT; i++) {
                db.beginTransaction();
                db.deleteAll();
                db.commitTransaction();
                Date date1 = new Date();
                db.beginTransaction();
                db.insert(data);
                db.commitTransaction();
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            double result = sum / (double) TEST_COUNT;
            emitter.onSuccess(result);
        });

        single
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<Double>() {
                    @Override
                    public void onSuccess(Double aDouble) {
                        resultsSubject.onNext(aDouble);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public Single<List<UserEntity>> testRealmLoadData() {
        return Single.create((SingleEmitter<List<UserEntity>> emitter) -> {
            List<UserEntity> realmResults = new ArrayList<>();
            long sum = 0;
            Realm db = Realm.getDefaultInstance();
            for (int i = 0; i < TEST_COUNT; i++) {
                Date date1 = new Date();
                realmResults = db.where(UserEntity.class).findAll();
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            List<UserEntity> data = db.copyFromRealm(realmResults);
            double result = sum / (double) TEST_COUNT;
            emitter.onSuccess(data);
            resultsSubject.onNext(result);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Double> subscribeOnResults() {
        return resultsSubject.observeOn(AndroidSchedulers.mainThread());
    }

}
