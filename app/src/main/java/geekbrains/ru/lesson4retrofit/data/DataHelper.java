package geekbrains.ru.lesson4retrofit.data;

import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.DBTests.TestHandler;
import geekbrains.ru.lesson4retrofit.DBTests.TestResult;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class DataHelper {
    private static final int TEST_COUNT = 100;
    @Inject
    RealmDataRepoImpl realmDataRepo;
    @Inject
    RoomDataRepoImpl roomDataRepo;
    @Inject
    TestHandler<UserEntity> testHandler;

    private PublishSubject<TestResult> resultsSubject = PublishSubject.create();


    public DataHelper(AppComponent component) {
        component.inject(this);
    }

    public Completable testRoomSaveData(List<UserEntity> data) {
        return Completable.create(emitter -> {
            resultsSubject.onNext(new TestResult());
            TestResult result = testHandler.run(testHandler.makeSaveTest(roomDataRepo, data), TEST_COUNT);
            resultsSubject.onNext(result);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<List<UserEntity>> testRoomLoadData() {
        resultsSubject.onNext(new TestResult());

        return Single.create((SingleEmitter<List<UserEntity>> emitter) -> {
            TestResult result = testHandler.run(testHandler.makeLoadTest(roomDataRepo), TEST_COUNT);
            emitter.onSuccess(testHandler.getData());
            resultsSubject.onNext(result);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable testRealmSaveData(List<UserEntity> data) {
        return Completable.create((emitter) -> {
            resultsSubject.onNext(new TestResult());
            TestResult result = testHandler.run(testHandler.makeSaveTest(realmDataRepo, data), TEST_COUNT);
            resultsSubject.onNext(result);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<List<UserEntity>> testRealmLoadData() {
        resultsSubject.onNext(new TestResult());
        return Single.create((SingleEmitter<List<UserEntity>> emitter) -> {
            TestResult result = testHandler.run(testHandler.makeLoadTest(realmDataRepo), TEST_COUNT);
            emitter.onSuccess(testHandler.getData());
            resultsSubject.onNext(result);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TestResult> subscribeOnResults() {
        return resultsSubject.observeOn(AndroidSchedulers.mainThread());
    }

}
