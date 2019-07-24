package com.reich.gutesvomstoll.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.reich.gutesvomstoll.R;

public class SoundsFragment extends ListFragment {

    private ArrayAdapter mAdapter;
    private String[] mSoundArray = {"Das muss man wissen!", "Ist kein Science-Fiction."}; //TODO: Get list of sounds from directory. Add sounds.

    public SoundsFragment() {
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
        mAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, mSoundArray);

        setListAdapter(mAdapter);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

}
