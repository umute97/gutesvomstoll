package com.reich.gutesvomstoll.ui.frag;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.reich.gutesvomstoll.R;
import com.reich.gutesvomstoll.util.Sound;
import com.reich.gutesvomstoll.util.SoundDBHelper;
import com.reich.gutesvomstoll.util.SoundListAdapter;

import java.util.List;

public abstract class BaseFragment extends ListFragment {

    private final String TAG = "com.reich.gutesvomstoll";

    public SoundListAdapter mAdapter;
    public List<Sound> mSounds;
    public SoundDBHelper mDBHelper;
    public MediaPlayer mMP;

    public BaseFragment(SoundDBHelper dbHelper) {

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

        View view = inflateFragment(inflater, container, savedInstanceState);
        return view;
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

    public void updateView(List<Sound> list)  {

        mSounds.clear();
        mSounds.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public void setupListAdapter()  {

        mAdapter = new SoundListAdapter(getActivity(),
                R.layout.sound_list_item, mSounds, mDBHelper);
        setListAdapter(mAdapter);
    }

    public abstract View inflateFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
