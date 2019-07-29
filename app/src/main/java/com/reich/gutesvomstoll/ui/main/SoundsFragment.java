package com.reich.gutesvomstoll.ui.main;

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

import java.lang.reflect.Field;

public class SoundsFragment extends ListFragment {

    private ArrayAdapter mAdapter;
    private Field[] mRawSounds = R.raw.class.getFields();
    private String[] mSoundNames = formatSoundNames(mRawSounds);
    private MediaPlayer mMP;

    public SoundsFragment() {}

    public ArrayAdapter getmAdapter() {

        return mAdapter;
    }

    private String[] formatSoundNames(Field[] rawSounds)  {

        String[] soundNames = new String[rawSounds.length];

        for (int i = 0; i < rawSounds.length; i++)  {

            soundNames[i] = rawSounds[i].getName().toUpperCase().replace("_", " ");
        }

        return soundNames;
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
        mAdapter = new ArrayAdapter(getActivity(),
                R.layout.sound_list_item, mSoundNames);

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
