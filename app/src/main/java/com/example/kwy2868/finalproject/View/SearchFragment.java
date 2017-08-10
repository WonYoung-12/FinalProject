package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.kwy2868.finalproject.Adapter.BlogContentAdapter;
import com.example.kwy2868.finalproject.Adapter.CafeArticleAdapter;
import com.example.kwy2868.finalproject.Adapter.KiNContentAdapter;
import com.example.kwy2868.finalproject.Model.BlogContent;
import com.example.kwy2868.finalproject.Model.CafeArticle;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NaverAPI.NaverAPIManager;
import com.example.kwy2868.finalproject.Retrofit.NaverAPI.SearchService;
import com.example.kwy2868.finalproject.Util.ParsingHelper;
import com.google.gson.JsonObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class SearchFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, View.OnKeyListener, TextView.OnEditorActionListener, PullRefreshLayout.OnRefreshListener {

    @BindView(R.id.searchSpinner)
    Spinner searchSpinner;
    @BindView(R.id.searchRefreshLayout)
    PullRefreshLayout searchRefreshLayout;
    @BindView(R.id.searchRecyclerView)
    RecyclerView searchRecyclerView;
    @BindView(R.id.searchButton)
    Button searchButton;
    @BindView(R.id.search_EditText)
    EditText searchEditText;

    // 블로그.
    private static final int BLOG = 0;
    // 카페.
    private static final int CAFE = 1;
    // 지식인.
    private static final int KIN = 2;

    // 네이버 API에 요구하는 아이템의 갯수. 10개가 디폴트 최대 100개.
    private static final int REQUEST_ITEM_COUNT = 20;

    private RecyclerView.LayoutManager layoutManager;

    private Unbinder unbinder;

    // 스피너에서 현재 선택되어있는 아이템.
    private int currentItem = BLOG;

    private List currentList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        searchSpinner.setOnItemSelectedListener(this);
        searchRefreshLayout.setOnRefreshListener(this);

        // 엔터키 이벤트
        searchEditText.setOnKeyListener(this);
        searchEditText.setOnEditorActionListener(this);

        if (savedInstanceState != null) {
            currentList = savedInstanceState.getParcelableArrayList("currentList");
            if (currentList != null)
                recyclerViewSetting(currentList);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // i = 0이면 블로그, 1이면 카페, 2이면 지식인. 이제 이걸로 나중에 비교하자. 검색창에서 클릭 버튼 눌렀을 때..!
        currentItem = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // 검색 버튼 눌렀을 때.
    @OnClick(R.id.searchButton)
    public void search() {
        String keyword = searchEditText.getText().toString();
        SearchService searchService = NaverAPIManager.getSearchService();

        // 아무 것도 입력 안했거나 공백만 입력한 경우.
        if (keyword.equals("") || keyword.trim().equals("")) {
            Snackbar.make(getActivity().getCurrentFocus(), "검색어를 1자 이상 입력해 주세요.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //Toast.makeText(getContext(), "검색어를 1자 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (currentItem) {
            case BLOG:
                blogSearch(searchService, keyword);
                break;
            case CAFE:
                cafeSearch(searchService, keyword);
                break;
            case KIN:
                kiNSearch(searchService, keyword);
                break;
        }
        searchRefreshLayout.setRefreshing(false);
    }

    // 엔터키 눌렀을 때도 검색이 되게 해주자.
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
            search();
            return true;
        }
        return false;
    }

    public void blogSearch(SearchService searchService, String keyword) {
        Call<JsonObject> call = searchService.getBlogContentList(keyword, REQUEST_ITEM_COUNT);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("성공", "성공");
                    JsonObject jsonObject = response.body();
                    recyclerViewSetting(ParsingHelper.searchParsing(jsonObject, BLOG));
                    Snackbar.make(getActivity().getCurrentFocus(), "네이버 블로그에서 유사한 20가지의 결과를 검색합니다.", Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("실패", "실패");
            }
        });
    }

    public void cafeSearch(SearchService searchService, String keyword) {
        Call<JsonObject> call = searchService.getCafeArticleList(keyword, REQUEST_ITEM_COUNT);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("성공", "성공");
                    JsonObject jsonObject = response.body();
                    recyclerViewSetting(ParsingHelper.searchParsing(jsonObject, CAFE));
                    Snackbar.make(getActivity().getCurrentFocus(), "네이버 카페에서 유사한 20가지의 결과를 검색합니다.", Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("실패", "실패");
            }
        });
    }

    public void kiNSearch(SearchService searchService, String keyword) {
        Call<JsonObject> call = searchService.getKinArticleList(keyword, REQUEST_ITEM_COUNT);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("성공", "성공");
                    JsonObject jsonObject = response.body();
                    recyclerViewSetting(ParsingHelper.searchParsing(jsonObject, KIN));
                    Snackbar.make(getActivity().getCurrentFocus(), "네이버 지식iN에서 유사한 20가지의 결과를 검색합니다.", Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("실패", "실패");
            }
        });
    }

    public void recyclerViewSetting(List list) {
        if(list == null){
            Toast.makeText(getActivity(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentList = list;

        layoutManager = new LinearLayoutManager(getContext());
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setHasFixedSize(true);

        if (list.get(0) instanceof BlogContent) {
            BlogContentAdapter blogContentAdapter = new BlogContentAdapter(list);
            searchRecyclerView.setAdapter(blogContentAdapter);
        } else if (list.get(0) instanceof CafeArticle) {
            CafeArticleAdapter cafeArticleAdapter = new CafeArticleAdapter(list);
            searchRecyclerView.setAdapter(cafeArticleAdapter);
        } else {
            KiNContentAdapter kiNContentAdapter = new KiNContentAdapter(list);
            searchRecyclerView.setAdapter(kiNContentAdapter);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            search();
            return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        search();
    }
}