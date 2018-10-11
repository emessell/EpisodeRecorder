package com.seop.episoderecorder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingerItemView extends LinearLayout {

    TextView textView;
    TextView textView2;
    String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SingerItemView(Context context) {
        super(context);
        init(context);
    }

    public SingerItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_singer_item,this,true);
        textView = (TextView) findViewById(R.id.title);
        textView2 = (TextView) findViewById(R.id.episode);
    }

    public void setTitle(String title){
        textView.setText(title);
    }

    public void setEpisode(String episode){
        textView2.setText(episode);
    }


}
