package com.reich.gutesvomstoll.ui.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reich.gutesvomstoll.R;
import com.reich.gutesvomstoll.util.SoundDBHelper;

public class SoundsFragment extends BaseFragment {

    public SoundsFragment(SoundDBHelper dbHelper) {

        super(dbHelper);
    }

    @Override
    public View inflateFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mSounds = mDBHelper.getSoundsFromDB();
        setupListAdapter();

        return view;
    }

}
