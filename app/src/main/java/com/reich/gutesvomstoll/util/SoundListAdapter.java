package com.reich.gutesvomstoll.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.reich.gutesvomstoll.R;

import java.util.ArrayList;
import java.util.List;

public class SoundListAdapter extends ArrayAdapter<Sound> implements Filterable {

    private List<Sound> mOriginalSounds;
    private List<Sound> mDisplayedSounds;
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
        this.mOriginalSounds = sounds;
        this.mDisplayedSounds = sounds;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)  {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mItemRes, parent, false);
        }

        final Sound sound = mDisplayedSounds.get(position);
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

    @Override
    public int getCount() {

        return mDisplayedSounds != null ? mDisplayedSounds.size() : 0;
    }

    @Override
    public Sound getItem(int position) {

        return mDisplayedSounds.get(position);
    }

    @Override
    public int getPosition(Sound item) {

        for(int i = 0; i < mDisplayedSounds.size(); i++)  {

            if(item == mDisplayedSounds.get(i))
                return i;

        }
        return super.getPosition(item);
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint)  {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0)  {

                    results.count = mOriginalSounds.size();
                    results.values = mOriginalSounds;

                } else  {

                    constraint = constraint.toString().toLowerCase();
                    List<Sound> filteredList = new ArrayList<Sound>();

                    for (Sound sound: mOriginalSounds)  {

                        String soundName = sound.getName();

                        if (soundName.toLowerCase().contains(constraint.toString()))  {

                            filteredList.add(new Sound(sound.getID(), sound.getName(), sound.isFave()));
                        }
                    }

                    results.count = filteredList.size();
                    results.values = filteredList;
                }

                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {

                mDisplayedSounds = (List<Sound>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;

    }
}
