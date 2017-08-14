package com.example.kwy2868.finalproject.Util;

import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.example.kwy2868.finalproject.Model.BaseModel;
import com.example.kwy2868.finalproject.Model.GlobalData;

/**
 * Created by kwy2868 on 2017-08-14.
 */

public class SearchResultClickListener {
    private BaseModel baseModel;

    public SearchResultClickListener(BaseModel baseModel) {
        this.baseModel = baseModel;
    }

    // 아이템 클릭했을 때.
    public void itemClick(){
        String url = baseModel.getLink();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
//        customTabsIntent.setAlwaysUseBrowserUI(customTabsIntent);
        customTabsIntent.launchUrl(GlobalData.getContext(), Uri.parse(url));
    }
}
