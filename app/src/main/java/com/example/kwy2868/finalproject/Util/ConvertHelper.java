package com.example.kwy2868.finalproject.Util;

import android.text.Html;

import com.example.kwy2868.finalproject.Model.BlogContent;
import com.example.kwy2868.finalproject.Model.CafeArticle;
import com.example.kwy2868.finalproject.Model.KiNContent;

import java.util.ArrayList;

/**
 * Created by kwy2868 on 2017-08-02.
 */

///// HTML 태그가 포함된 String들을 변환해주는 클래스./////

public class ConvertHelper {

    public static void convert(ArrayList list){
        // BlogContent인 경우.
        if(list.get(0) instanceof BlogContent){
            for(int i=0; i<list.size(); i++){
                BlogContent blogContent = (BlogContent) list.get(i);

                blogContent.setTitle(Html.fromHtml(blogContent.getTitle()).toString());
                blogContent.setLink(Html.fromHtml(blogContent.getLink()).toString());
                blogContent.setDescription(Html.fromHtml(blogContent.getDescription()).toString());
                blogContent.setBloggername(Html.fromHtml(blogContent.getBloggername()).toString());
                blogContent.setBloggerlink(Html.fromHtml(blogContent.getBloggerlink()).toString());
                blogContent.setPostdate(Html.fromHtml(blogContent.getPostdate()).toString());
            }
        }
        // CafeArticle인 경우.
        else if(list.get(0) instanceof CafeArticle){
            for(int i=0; i<list.size(); i++){
                CafeArticle cafeArticle = (CafeArticle) list.get(i);

                cafeArticle.setTitle(Html.fromHtml(cafeArticle.getTitle().toString()).toString());
                cafeArticle.setLink(Html.fromHtml(cafeArticle.getLink().toString()).toString());
                cafeArticle.setDescription(Html.fromHtml(cafeArticle.getDescription().toString()).toString());
                cafeArticle.setCafename(Html.fromHtml(cafeArticle.getCafename().toString()).toString());
                cafeArticle.setCafeurl(Html.fromHtml(cafeArticle.getCafeurl().toString()).toString());
            }
        }
        // 지식인인 경우.
        else{
            for(int i=0; i<list.size(); i++){
                KiNContent kiNContent = (KiNContent) list.get(i);

                kiNContent.setTitle(Html.fromHtml(kiNContent.getTitle().toString()).toString());
                kiNContent.setLink(Html.fromHtml(kiNContent.getLink().toString()).toString());
                kiNContent.setDescription(Html.fromHtml(kiNContent.getDescription().toString()).toString());
            }
        }
    }
}
