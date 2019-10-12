package geekbrains.ru.lesson4retrofit.presenters;

import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.data.NetworkHelper;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.di.AppComponent;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class UsersPresenter {
    private CompositeDisposable bag = new CompositeDisposable();
    private Observer<Boolean> progress;
    private Observer<List<RepoEntity>> data;
    private String currentUser;

    @Inject
    NetworkHelper helper;

    public UsersPresenter(AppComponent component) {
        component.inject(this);
    }

    public void bindView(
            DisposableObserver<Boolean> progress,
            DisposableObserver<List<RepoEntity>> data,
            String name) {
        this.progress = progress;
        this.data = data;

        initView(name);
    }

    public void unbindView() {
        bag.clear();
    }

    private void initView(String name) {
        if (currentUser != null && currentUser.equals(name)) {
            data.onNext(helper.getCurrentRepos());
            return;
        }
        this.currentUser = name;
        getRepos(name);
    }

    private void getRepos(String name) {
        Disposable d = helper.getRepos(name).subscribeWith(new DisposableSingleObserver<List<RepoEntity>>() {
            @Override
            protected void onStart() {
                super.onStart();
                progress.onNext(true);
            }

            @Override
            public void onSuccess(List<RepoEntity> repoEntities) {
                progress.onNext(false);
                data.onNext(repoEntities);
                helper.setCurrentRepos(repoEntities);
            }

            @Override
            public void onError(Throwable e) {
                progress.onError(e);
            }
        });
        bag.add(d);
    }
}
