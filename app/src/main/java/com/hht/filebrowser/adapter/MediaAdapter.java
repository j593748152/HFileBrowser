package com.hht.filebrowser.adapter;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import com.hht.uc.FileBrowser.R;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MediaAdapter extends BaseAdapter {
	private List<String> mMusicList;
	private Context mContext;

	public MediaAdapter(Context context, List<String> list) {
		mContext = context;
		mMusicList = list;
	}
	@Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (LinearLayout) inflater.inflate(R.layout.dialog_music_item, null);
        }else {
            layout = (LinearLayout) convertView;
        }
        TextView tvName = (TextView) layout.findViewById(R.id.text_music_name);
        TextView tvTime = (TextView) layout.findViewById(R.id.item_media_time);
        File file = new File(mMusicList.get(position).toString());
        tvName.setText(file.getName());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mMusicList.get(position).toString());
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        tvTime.setText(getCurrentTime(Integer.valueOf(duration)));
        return layout;
    }
    private String getCurrentTime(int time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }
}
