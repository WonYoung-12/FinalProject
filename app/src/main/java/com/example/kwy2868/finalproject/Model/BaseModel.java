package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;

/**
 * Created by kwy2868 on 2017-08-02.
 */

@Parcel(Parcel.Serialization.BEAN)
public class BaseModel {
    // 글 제목.
    protected String title;
    // 글 링크.
    protected String link;
    // 글 요약.
    protected String description;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
