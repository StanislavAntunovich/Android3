package geekbrains.ru.lesson4retrofit.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

@Entity
public class UserEntity extends RealmObject implements Serializable {

    @io.realm.annotations.PrimaryKey
    @PrimaryKey
    @NonNull
    private Integer id;

    private String login;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String url;

    @Ignore
    public UserEntity() {
    }

    public UserEntity(@NonNull Integer id, String login, String avatarUrl, String url) {
        this.id = id;
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}