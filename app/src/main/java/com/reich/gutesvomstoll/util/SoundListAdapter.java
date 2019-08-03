package com.reich.gutesvomstoll.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.reich.gutesvomstoll.R;

import java.util.List;

public class SoundListAdapter extends ArrayAdapter<String> {

    private List<String> mList;
    private Context mContext;
    private final int mItemRes;
    private SoundDBHelper mDBHelper;

    public SoundListAdapter(Context context,
                            int item_res,
                            List<String> soundNames,
                            SoundDBHelper dbHelper) {

        super(context, item_res, soundNames);
        this.mContext = context;
        this.mItemRes = item_res;
        this.mDBHelper = dbHelper;
        this.mList = soundNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)  {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mItemRes, parent, false);
        }

        String soundName = getItem(position);

        if(soundName != null)  {

            TextView tv_soundName = convertView.findViewById(R.id.sound_text);

            if(tv_soundName != null)
                tv_soundName.setText(soundName);
        }

        boolean isFav = mDBHelper.checkFave(soundName);

        CheckBox cb_fave = convertView.findViewById(R.id.fav_check);

        if(cb_fave != null)
            cb_fave.setChecked(isFav);

        return convertView;
    }
}
