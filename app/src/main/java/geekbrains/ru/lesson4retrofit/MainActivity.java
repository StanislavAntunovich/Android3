package geekbrains.ru.lesson4retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.Stetho;

import java.util.List;

import geekbrains.ru.lesson4retrofit.adapters.UsersAdapter;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.di.DaggerDataComponent;
import geekbrains.ru.lesson4retrofit.di.DataComponent;
import geekbrains.ru.lesson4retrofit.di.modules.ActivityModule;
import geekbrains.ru.lesson4retrofit.di.modules.ApplicationContextModule;
import geekbrains.ru.lesson4retrofit.presenters.MainPresenter;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME_KEY = "user name";
    public static final String REPO_MODEL_KEY = "repo_model_key";

    private UsersAdapter adapter;
    private MainPresenter presenter;

    private ProgressBar progressBar;
    private EditText etUserName;
    private Double timeResult = 0d;
    private DataComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Realm.init(getApplicationContext());

        initPresenter();

        initRecycler();
        initViews();
    }

    @Override
    protected void onResume() {
        presenter.bindView(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unbindView();
    }

    private void initPresenter() {
        component = DaggerDataComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationContextModule(new ApplicationContextModule(this))
                .build();
        presenter = new MainPresenter();
        component.injectToPresenter(presenter);
    }

    private void initRecycler() {
        adapter = new UsersAdapter(model -> presenter.onItemClicked(model));
        RecyclerView recyclerView = findViewById(R.id.rv_users);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initViews() {
        etUserName = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar);

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(v -> onClick());

        Button bntSaveRoom = findViewById(R.id.btn_save_room);
        bntSaveRoom.setOnClickListener(v -> presenter.onRoomSaveClicked(adapter.getData()));

        Button btnSaveRealm = findViewById(R.id.btn_save_realm);
        btnSaveRealm.setOnClickListener(v -> presenter.onRealmSaveClicked(adapter.getData()));

        Button btnLoadRoom = findViewById(R.id.btn_load_room);
        btnLoadRoom.setOnClickListener(v -> presenter.onRoomLoadClicked());

        Button btnLoadRealm = findViewById(R.id.btn_load_realm);
        btnLoadRealm.setOnClickListener(v -> presenter.onRealmLoadClicked());
    }

    //TODO
    public void startUserReposActivity(String userName) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(USER_NAME_KEY, userName);
        startActivity(intent);
    }

    public boolean isNetworkConnected() {
        return component.isConnected();
    }

    public void onClick() {
        String request = etUserName.getText().toString();
        etUserName.setText("");
        presenter.loadNetData(request);
    }

    public void updateResult() {
        TextView tvResult = findViewById(R.id.tv_test_time);
        TextView tvCount = findViewById(R.id.tv_test_items_count);
        tvResult.setText(String.valueOf(timeResult));
        tvCount.setText(String.valueOf(adapter.getData().size()));
    }

    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public void setResult(Double result) {
        this.timeResult = result;
    }

    public void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    public void updateRecyclerData(List<UserEntity> data) {
        adapter.setData(data);
    }

    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

}
