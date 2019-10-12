package geekbrains.ru.lesson4retrofit.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;

public class DataHelper {
    private static final int TEST_COUNT = 100;
    @Inject
    RoomDB roomDB;

    private PublishSubject<String> resultsSubject = PublishSubject.create();


    public DataHelper(AppComponent component) {
        component.inject(this);
    }

    public Completable testRoomSaveData(List<UserEntity> data) {
        return Completable.create(emitter -> {
            resultsSubject.onNext("");
            long sum = 0;
            for (int i = 0; i < TEST_COUNT; i++) {
                roomDB.getUsersDao().deleteAll();
                Date date1 = new Date();
                roomDB.getUsersDao().saveAll(data);
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            double result = sum / (double) TEST_COUNT;
            resultsSubject.onNext(String.valueOf(result));
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<List<UserEntity>> testRoomLoadData() {
        resultsSubject.onNext("");

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
            resultsSubject.onNext(String.valueOf(result));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable testRealmSaveData(List<UserEntity> data) {
        return Completable.create((emitter) -> {
            resultsSubject.onNext("");
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
            resultsSubject.onNext(String.valueOf(result));
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<List<UserEntity>> testRealmLoadData() {
        resultsSubject.onNext("");
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
            resultsSubject.onNext(String.valueOf(result));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> subscribeOnResults() {
        return resultsSubject.observeOn(AndroidSchedulers.mainThread());
    }

}
