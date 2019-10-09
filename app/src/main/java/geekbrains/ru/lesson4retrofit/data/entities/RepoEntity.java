package geekbrains.ru.lesson4retrofit.data.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RepoEntity implements Serializable {

    private Long id;
    private String name;
    @SerializedName("private")
    private Boolean isPrivate;
    private String language;
    @SerializedName("html_url")
    private String url;
    private String description;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("pushed_at")
    private String pushedAt;

    public RepoEntity(Long id, String name, Boolean isPrivate, String language, String url) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.language = language;
        this.url = url;
    }

    public RepoEntity() {}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean _private) {
        this.isPrivate = _private;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(String pushedAt) {
        this.pushedAt = pushedAt;
    }

}
