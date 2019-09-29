package geekbrains.ru.lesson4retrofit;

import java.util.ArrayList;
import java.util.List;

import geekbrains.ru.lesson4retrofit.data.DataWorker;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter {
    private MainActivity view;
    private DataWorker model;
    private CompositeDisposable bag = new CompositeDisposable();

    private DisposableSingleObserver<List<UserEntity>> getDataObserver() {
        return new DisposableSingleObserver<List<UserEntity>>() {
            @Override
            public void onSuccess(List<UserEntity> data) {
                onProcessFinished(data);
            }

            @Override
            public void onError(Throwable e) {
                view.stopProgress();
                e.printStackTrace();
            }
        };
    }

    private void onProcessFinished(List<UserEntity> data) {
        view.updateRecyclerData(data);
        view.updateRecyclerView();
        view.stopProgress();
    }

    public void loadNetData(String request) {
        view.startProgress();
        if (request.isEmpty()) {
            loadAllUsers();
        } else {
            loadSingleUser(request);
        }
    }

    private void loadAllUsers() {
        model.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDataObserver());
    }

    private void loadSingleUser(String name) {
        model.loadUser(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<UserEntity>() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        List<UserEntity> singleList = new ArrayList<>(1);
                        singleList.add(userEntity);
                        onProcessFinished(singleList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.stopProgress();
                    }
                });
    }

    public void onRoomSaveClicked(List<UserEntity> data) {
        view.startProgress();
        model.testRoomSaveData(data);
    }

    public void onRealmSaveClicked(List<UserEntity> data) {
        view.startProgress();
        model.testRealmSaveData(data);
    }


    public void onRoomLoadClicked() {
        view.startProgress();
        model.testRoomLoadData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDataObserver());
    }

    public void onRealmLoadClicked() {
        view.startProgress();
        model.testRealmLoadData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDataObserver());
    }

    public void bindView(MainActivity view) {
        this.view = view;
        Disposable d = model.subscribeOnResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            view.setResult(result);
                            view.updateResult();
                            view.stopProgress();
                        },
                        e -> e.printStackTrace()
                );
        bag.add(d);
    }

    public void unbindView() {
        this.view = null;
        bag.clear();
    }

    public void setModel(DataWorker model) {
        this.model = model;
    }

}
