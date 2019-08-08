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

public class SoundListAdapter extends ArrayAdapter<Sound> {

    private List<Sound> mList;
    private Context mContext;
    private final int mItemRes;
    private SoundDBHelper mDBHelper;

    public SoundListAdapter(Context context,
                            int item_res,
                            List<Sound> sounds,
                            SoundDBHelper dbHelper) {

        super(context, item_res, sounds);
        this.mContext = context;
        this.mItemRes = item_res;
        this.mDBHelper = dbHelper;
        this.mList = sounds;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)  {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mItemRes, parent, false);
        }

        final Sound sound = mList.get(position);
        String soundName = sound.getName();

        if(soundName != null)  {

            TextView tv_soundName = convertView.findViewById(R.id.sound_text);

            if(tv_soundName != null)
                tv_soundName.setText(soundName);
        }

        boolean isFav = sound.isFave();

        final CheckBox cb_fave = convertView.findViewById(R.id.fav_check);

        if(cb_fave != null)
            cb_fave.setChecked(isFav);

        cb_fave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final boolean isChecked = cb_fave.isChecked();
                mDBHelper.setFave(sound, isChecked);
                sound.setIsFave(isChecked);

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    // TODO: Implement Searchable interface and filter method
}
