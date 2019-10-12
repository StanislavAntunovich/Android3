package geekbrains.ru.lesson4retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.Stetho;

import javax.inject.Inject;

import geekbrains.ru.lesson4retrofit.adapters.UsersAdapter;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME_KEY = "user name";
    public static final String REPO_MODEL_KEY = "repo_model_key";

    private UsersAdapter adapter;

    @Inject
    MainPresenter presenter;

    private ProgressBar progressBar;
    private EditText etUserName;

    private TextView tvResult;
    private TextView tvCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);
        Realm.init(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progressBar = findViewById(R.id.progressBar);
        etUserName = findViewById(R.id.editText);

        tvResult = findViewById(R.id.tv_test_time);
        tvCount = findViewById(R.id.tv_test_items_count);

        GitHubApp.getComponent().inject(this);

        initRecycler();
        findViewById(R.id.btnLoad).setOnClickListener(this::onClick);
        findViewById(R.id.btn_save_room).setOnClickListener(v -> presenter.saveAllRoom());
        findViewById(R.id.btn_save_realm).setOnClickListener(v -> presenter.saveAllRealm());
        findViewById(R.id.btn_load_room).setOnClickListener(v -> presenter.loadAllRoom());
        findViewById(R.id.btn_load_realm).setOnClickListener(v -> presenter.loadAllRealm());

    }

    private void initRecycler() {
        RecyclerView rv = findViewById(R.id.rv_users);
        adapter = new UsersAdapter(new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                startUserReposActivity(s);
            }

            @Override
            public void onError(Throwable e) {
                showError(e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.bindView(getProgressObserver(), getResultObserver(), adapter.subscribe());
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unBindView();
        adapter.unsubscribe();
    }

    public void startUserReposActivity(String userName) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(USER_NAME_KEY, userName);
        startActivity(intent);
    }

    public void onClick(View view) {
        String request = etUserName.getText().toString();
        etUserName.setText("");
        presenter.loadNetData(request);
    }

    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void showProgress(boolean toShow) {
        progressBar.setVisibility(toShow ? View.VISIBLE : View.GONE);
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

    private DisposableObserver<String> getResultObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String aDouble) {
                tvResult.setText(String.valueOf(aDouble));
                if (!aDouble.isEmpty()) {
                    tvCount.setText(String.valueOf(adapter.getItemCount()));
                } else {
                    tvCount.setText("");
                }
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
