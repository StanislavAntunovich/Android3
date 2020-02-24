package geekbrains.ru.lesson4retrofit.presenters;

import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.DBTests.TestResult;
import geekbrains.ru.lesson4retrofit.data.DataHelper;
import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import geekbrains.ru.lesson4retrofit.di.NetworkComponent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class MainPresenter {
    private CompositeDisposable bag = new CompositeDisposable();
    private DisposableObserver<Boolean> progress;
    private DisposableObserver<List<UserEntity>> data;
    private NetworkComponent networkComponent;

    @Inject
    NetworkHelper networkHelper;
    @Inject
    DataHelper dataHelper;


    public MainPresenter(AppComponent component) {
        component.inject(this);
    }

    public void bindView(
            DisposableObserver<Boolean> progress,
            DisposableObserver<TestResult> result,
            DisposableObserver<List<UserEntity>> data,
            NetworkComponent networkComponent
    ) {
        this.progress = progress;
        this.data = data;
        this.networkComponent = networkComponent;

        bag.add(this.data);
        bag.add(this.progress);
        bag.add(result);
        bag.add(dataHelper.subscribeOnResults().subscribeWith(result));
        initView();
    }

    private void initView() {
        List<UserEntity> data = networkHelper.getCurrentUsers();
        if (!data.isEmpty()) {
            this.data.onNext(data);
        }
    }

    public void unBindView() {
        bag.clear();
        networkComponent = null;
    }

    public void loadNetData(String request) {
        if (!networkComponent.isConnected()) {
            progress.onError(new IllegalStateException("check connection"));
            return;
        }

        if (request.isEmpty()) {
            networkHelper.getAllUsers().subscribe(getObserver());
        } else {
            networkHelper.loadUser(request).subscribe(getObserver());
        }
    }

    public void saveAllRoom() {
        dataHelper.testRoomSaveData(networkHelper.getCurrentUsers())
                .subscribe(getCompletable());
    }

    public void saveAllRealm() {
        dataHelper.testRealmSaveData(networkHelper.getCurrentUsers())
                .subscribe(getCompletable());
    }

    public void loadAllRoom() {
        dataHelper
                .testRoomLoadData()
                .subscribe(getObserver());

    }

    public void loadAllRealm() {
        dataHelper
                .testRealmLoadData()
                .subscribe(getObserver());

    }

    private DisposableCompletableObserver getCompletable() {
        return new DisposableCompletableObserver() {
            @Override
            protected void onStart() {
                progress.onNext(true);
                super.onStart();
            }

            @Override
            public void onComplete() {
                progress.onNext(false);
            }

            @Override
            public void onError(Throwable e) {
                progress.onNext(false);
                progress.onError(e);
            }
        };
    }

    private DisposableSingleObserver<List<UserEntity>> getObserver() {
        return new DisposableSingleObserver<List<UserEntity>>() {
            @Override
            protected void onStart() {
                super.onStart();
                progress.onNext(true);
            }

            @Override
            public void onSuccess(List<UserEntity> userEntities) {
                networkHelper.setCurrentUsers(userEntities);
                progress.onNext(false);
                data.onNext(userEntities);
            }

            @Override
            public void onError(Throwable e) {
                progress.onNext(false);
                progress.onError(e);
            }
        };
    }

}
