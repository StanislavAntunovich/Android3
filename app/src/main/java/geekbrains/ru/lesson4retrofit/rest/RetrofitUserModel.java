package geekbrains.ru.lesson4retrofit.rest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RetrofitUserModel implements Serializable {

    private String login;
    private Integer id;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String url;

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