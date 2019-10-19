package geekbrains.ru.lesson4retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.adapters.ReposAdapter;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;
import io.reactivex.observers.DisposableObserver;

public class UserActivity extends AppCompatActivity {
    private ReposAdapter adapter;

    @Inject
    UsersPresenter presenter;

    private ProgressBar progressBar;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        name = getIntent().getStringExtra(MainActivity.USER_NAME_KEY);
        TextView tv = findViewById(R.id.tv_user_name_repos);
        tv.setText(name);

        progressBar = findViewById(R.id.pb_user);
        initRecycler();

        GitHubApp.getComponent().inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.bindView(getProgressObserver(), adapter.subscribe(), name);
        adapter.subscribeOnClick(new DisposableObserver<RepoEntity>() {
            @Override
            public void onNext(RepoEntity repoEntity) {
                startRepoActivity(repoEntity);
            }

            @Override
            public void onError(Throwable e) {
                showError(e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unbindView();
        adapter.unsubscribe();
    }

    private void initRecycler() {
        adapter = new ReposAdapter();

        RecyclerView rv = findViewById(R.id.rv_repos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void startRepoActivity(RepoEntity repoEntity) {
        Intent intent = new Intent(this, RepoActivity.class);
        intent.putExtra(MainActivity.REPO_MODEL_KEY, repoEntity);
        startActivity(intent);
    }

    private void showProgress(boolean toShow) {
        progressBar.setVisibility(toShow ? View.VISIBLE : View.GONE);
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private DisposableObserver<Boolean> getProgressObserver() {
        return new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                showProgress(aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                showError(e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
            }
        };
    }

}
