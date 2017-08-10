package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-09.
 */

// 서버에서 resultCode랑 reviewNum을 받고, reviewNum을 Review 객체에 set해주자.
public class WriteResult extends BaseResult{
    private int reviewNum;

    public int getReviewNum() {
        return reviewNum;
    }

    @Override
    public String toString() {
        return "WriteResult{" +
                "reviewNum=" + reviewNum +
                '}';
    }
}
