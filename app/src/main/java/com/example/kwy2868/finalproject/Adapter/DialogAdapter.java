package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.DialogViewHolder;

/**
 * Created by kwy2868 on 2017-08-14.
 */

public class DialogAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    // 자동차, 대중교통, 도보 3가지 방식.
    private static final int COUNT = 3;

    public DialogAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        DialogViewHolder dialogViewHolder = new DialogViewHolder();

        if(view == null){
            view = layoutInflater.inflate(R.layout.dialog_item, viewGroup, false);
            dialogViewHolder.setImageView((ImageView) view.findViewById(R.id.dialogImage));
            dialogViewHolder.setTextView((TextView) view.findViewById(R.id.dialogText));
        }
        ImageView dialogImage = dialogViewHolder.getImageView();
        TextView dialogText = dialogViewHolder.getTextView();

        switch (position){
            // 자동차.
            case 0:
                dialogImage.setImageResource(R.mipmap.ic_car);
                dialogText.setText("자동차");
                break;
            // 대중교통.
            case 1:
                dialogImage.setImageResource(R.mipmap.ic_bus);
                dialogText.setText("대중교통");
                break;
            // 도보.
            case 2:
                dialogImage.setImageResource(R.mipmap.ic_walk);
                dialogText.setText("도보");
                break;
        }
        return view;
    }
}
