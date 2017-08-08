package com.example.kwy2868.finalproject.Util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by kwy2868 on 2017-08-07.
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    //    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
//        this.mLayoutManager = layoutManager;
//        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
//    }
//
//    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
//        this.mLayoutManager = layoutManager;
//        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
//    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        // 리사이클러뷰 아이템의 총 갯수. 15개만 가져오네.
        int totalItemCount = mLayoutManager.getItemCount();

        // 마지막에 보이는 뷰의 포지션을 반환한다.
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            // get maximum element within the list
            // Staggered라 정확한 이해가 당장 필요하지는 않지만 나중에 하자.
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            Log.d("마지막에 보이는 아이템", "Position : " + lastVisibleItemPosition);
            Log.d("총 아이템 갯수", "갯수 : " + totalItemCount);
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            Log.d("현재 페이지", "페이지 : " + this.currentPage);
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
                Log.d("로딩중", "로딩중");
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        // 처음 리사이클러뷰 시작할 때겠지..? 근데 이 totalItemCount는 바뀌나? 계속 15개인 것 같은데.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            Log.d("로딩중인데 total보다 많다.", "처음이겠지?");
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        // 두 번째..?
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            Log.d("받아오자", "다음 페이지 내용 미리 받아오자");
            currentPage++;
            onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    // Call this method whenever performing new searches
    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    // Defines the process for actually loading more data based on page
    // 이제 여기를 하자.
    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

}