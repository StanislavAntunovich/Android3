package geekbrains.ru.lesson4retrofit.presenters;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.UserActivity;
import geekbrains.ru.lesson4retrofit.data.UsersDataHelper;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import io.reactivex.observers.DisposableSingleObserver;

public class UsersPresenter {
    private UserActivity view;

    @Inject
    public UsersDataHelper helper;

    public void bindView(UserActivity view) {
        this.view = view;
    }

    public void unbindView() {
        this.view = null;
    }

    public void getRepos(String name) {
        if (!view.isNetworkConnected()) {
            view.showError("check internet connection");
            return;
        }
        view.startLoading();
        helper.getRepos(name).subscribe(new DisposableSingleObserver<List<RepoEntity>>() {
            @Override
            public void onSuccess(List<RepoEntity> repoEntities) {
                view.stopLoading();
                view.updateData(repoEntities);
            }

            @Override
            public void onError(Throwable e) {
                view.stopLoading();
                view.showError(e.getLocalizedMessage());
            }
        });
    }

    public void onRepoClick(Serializable repo) {
        view.startRepoActivity(repo);
    }

    public void drop() {
        helper = null;
    }
}
