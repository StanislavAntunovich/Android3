package geekbrains.ru.lesson4retrofit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import geekbrains.ru.lesson4retrofit.rest.RestApi;
import geekbrains.ru.lesson4retrofit.rest.RetrofitRepoModel;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private CompositeDisposable bag = new CompositeDisposable();

    private ReposAdapter adapter;

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText etUserName;

    private Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private RestApi api = retrofit.create(RestApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        etUserName = findViewById(R.id.editText);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener((v) -> onClick());

        adapter = new ReposAdapter(this::startRepoActivity);
        RecyclerView recyclerView = findViewById(R.id.rv_repos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        bag.clear();
        super.onDestroy();
    }

    private void startRepoActivity(RetrofitRepoModel model) {
        Intent intent = new Intent(this, RepoActivity.class);
        intent.putExtra(RepoActivity.REPO_MODEL_KEY, model);
        startActivity(intent);
    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager != null
                ? connectivityManager.getActiveNetworkInfo() : null;
        if (networkinfo == null || !networkinfo.isConnected()) {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void onClick() {
        if (checkInternet()) return;
        progressBar.setVisibility(View.VISIBLE);
        downloadOneUrl(etUserName.getText().toString());
    }

    private void downloadOneUrl(String request) {
        if (request.isEmpty()) {
            Toast.makeText(this, R.string.fill_user_name, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        api.getUserRepos(request)
                .subscribeOn(Schedulers.io())
                .retry(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<RetrofitRepoModel>>() {
            @Override
            public void onSubscribe(Disposable d) {
                bag.add(d);
            }

            @Override
            public void onSuccess(List<RetrofitRepoModel> data) {
                mInfoTextView.setText(request);
                adapter.setData(data);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                mInfoTextView.setText(R.string.error);
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}
