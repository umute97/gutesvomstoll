package com.reich.gutesvomstoll.ui.frag;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.reich.gutesvomstoll.R;
import com.reich.gutesvomstoll.util.Sound;
import com.reich.gutesvomstoll.util.SoundDBHelper;
import com.reich.gutesvomstoll.util.SoundListAdapter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class FavsFragment extends ListFragment {

    private final String TAG = "com.reich.gutesvomstoll";

    private SoundListAdapter mAdapter;
    private List<Sound> mSounds;
    public MediaPlayer mMP;
    private SoundDBHelper mDBHelper;

    public FavsFragment(SoundDBHelper dbHelper) {

        this.mDBHelper = dbHelper;
    }

    public ArrayAdapter getmAdapter() {

        return mAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Get this ListFragments associated List and bind the Adapter
        mSounds = mDBHelper.getFavesFromDB();

        mAdapter = new SoundListAdapter(getActivity(),
                R.layout.sound_list_item, mSounds, mDBHelper);

        setListAdapter(mAdapter);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Sound sound = mAdapter.getItem(position);

        mMP = MediaPlayer.create(getActivity().getApplicationContext(), sound.getID());
        mMP.start();

        mMP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mp.release();

            };
        });
    }

    public void updateView()  {

        mSounds.clear();
        mSounds.addAll(mDBHelper.getFavesFromDB());
        mAdapter.notifyDataSetChanged();
    }
}

