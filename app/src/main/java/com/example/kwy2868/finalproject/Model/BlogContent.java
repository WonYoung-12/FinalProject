package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;

/**
 * Created by kwy2868 on 2017-08-02.
 */
@Parcel(Parcel.Serialization.BEAN)
public class BlogContent extends BaseModel{
    // 작성자 이름.
    private String bloggername;

    // 작성자 블로그.
    private String bloggerlink;

    // 포스팅한 날짜.
    private String postdate;

    public String getBloggername() {
        return bloggername;
    }

    public String getBloggerlink() {
        return bloggerlink;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setBloggername(String bloggername) {
        this.bloggername = bloggername;
    }

    public void setBloggerlink(String bloggerlink) {
        this.bloggerlink = bloggerlink;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }
}
