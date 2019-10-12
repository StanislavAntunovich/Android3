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

import java.io.Serializable;
import java.util.List;

import geekbrains.ru.lesson4retrofit.adapters.ReposAdapter;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.di.DaggerDataComponent;
import geekbrains.ru.lesson4retrofit.di.DataComponent;
import geekbrains.ru.lesson4retrofit.di.modules.ActivityModule;
import geekbrains.ru.lesson4retrofit.di.modules.ApplicationContextModule;
import geekbrains.ru.lesson4retrofit.presenters.UsersPresenter;

import static geekbrains.ru.lesson4retrofit.MainActivity.REPO_MODEL_KEY;

public class UserActivity extends AppCompatActivity {
    private ReposAdapter adapter;
    private UsersPresenter presenter;

    private ProgressBar progressBar;
    private String name;

    private DataComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        progressBar = findViewById(R.id.pb_user);

        name = getIntent().getStringExtra(MainActivity.USER_NAME_KEY);

        component = DaggerDataComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationContextModule(new ApplicationContextModule(this))
                .build();

        TextView tv = findViewById(R.id.tv_user_name_repos);
        tv.setText(name);

        initRecycler();
        initPresenter();

    }

    @Override
    protected void onResume() {
        presenter.bindView(this);
        presenter.getRepos(name);
        super.onResume();
    }

    @Override
    protected void onPause() {
        presenter.unbindView();
        super.onPause();
    }

    private void initPresenter() {
        presenter = new UsersPresenter();
        component.injectToUserPresenter(presenter);
    }

    private void initRecycler() {
        RecyclerView rv = findViewById(R.id.rv_repos);
        adapter = new ReposAdapter((model) -> presenter.onRepoClick(model));
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean isNetworkConnected() {
        return component.isConnected();
    }

    public void startRepoActivity(Serializable repo) {
        Intent intent = new Intent(this, RepoActivity.class);
        intent.putExtra(REPO_MODEL_KEY, repo);
        startActivity(intent);
    }

    public void updateData(List<RepoEntity> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    public void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        component = null;
        presenter.drop();
        super.onDestroy();
    }
}
