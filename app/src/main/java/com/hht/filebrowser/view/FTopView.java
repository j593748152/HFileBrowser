package com.hht.filebrowser.view;

import com.hht.uc.FileBrowser.R;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * Created by DK-dong on 2014/11/17.
 */
public class FTopView extends RelativeLayout implements View.OnClickListener{
    private Context mContext;
    private ImageView mClose;
    public FTopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        init();
    }
    public FTopView(Context context){
        super(context);
        mContext = context;
        init();
    }

    public void init(){
        LayoutInflater.from(mContext).inflate(R.layout.layout_top_view, this);
        mClose = (ImageView) findViewById(R.id.image_close);
        mClose.setOnClickListener(this);
        this.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_close:
                Intent intent = new Intent();
                intent.setAction("com.hht.fileBrowser.ACTION.FINISH");
                mContext.sendBroadcast(intent);
                break;
        }
    }
}
