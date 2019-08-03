package com.reich.gutesvomstoll.ui.frag;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.reich.gutesvomstoll.util.SoundDBHelper;
import com.reich.gutesvomstoll.util.SoundListAdapter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class SoundsFragment extends ListFragment {

    private SoundListAdapter mAdapter;
    private Field[] mRawSounds = R.raw.class.getFields();
    private List<String> mSoundNames = formatSoundNames(mRawSounds);
    public MediaPlayer mMP;
    private SoundDBHelper mDBHelper;

    public SoundsFragment(SoundDBHelper dbHelper) {

        this.mDBHelper = dbHelper;
    }

    public ArrayAdapter getmAdapter() {

        return mAdapter;
    }

    private List<String> formatSoundNames(Field[] rawSounds)  {

        String[] soundNames = new String[rawSounds.length];

        for (int i = 0; i < rawSounds.length; i++)  {

            soundNames[i] = rawSounds[i].getName().toUpperCase().replace("_", " ");
        }

        return Arrays.asList(soundNames);
    }

    private String convertToRawName(String soundName)  {

        return soundName.toLowerCase().replace(" ", "_");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Get this ListFragments associated List and bind the Adapter
        mAdapter = new SoundListAdapter(getActivity(),
                R.layout.sound_list_item, mSoundNames, mDBHelper);

        setListAdapter(mAdapter);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView soundName = v.findViewById(R.id.sound_text);

        String selItem = convertToRawName(soundName.getText().toString());

        Resources res = getActivity().getApplicationContext().getResources();
        int soundID = res.getIdentifier(selItem, "raw", getActivity().getApplicationContext().getPackageName());

        mMP = MediaPlayer.create(getActivity().getApplicationContext(), soundID);
        mMP.start();

        mMP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mp.release();

            };
        });
    }
}
