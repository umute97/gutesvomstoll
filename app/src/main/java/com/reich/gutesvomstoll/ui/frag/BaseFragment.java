package com.reich.gutesvomstoll.ui.frag;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.ListFragment;

import com.reich.gutesvomstoll.R;
import com.reich.gutesvomstoll.util.Sound;
import com.reich.gutesvomstoll.util.SoundDBHelper;
import com.reich.gutesvomstoll.util.SoundListAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/*  The base fragment class
    Since both fragments are more or less the same (hold sound entries in list, play them back, etc.)
    with the only difference in actual Sound/Favorite differentiation in the database, we just create
    an abstract base class that children can inherit from.
    Differences manifest themselves in different implementation of the abstract method "inflateFragment".
 */
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

    // Handles a long press on any list item. Share function is implemented here.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String fileName = convertNameToRaw(mAdapter.getItem(i).getName()) + ".mp3";

                // Get temp dir to stream the shared sound from, make dirs if they do not exist
                File storage = getActivity().getExternalFilesDir(null);
                File directory = new File(storage.getAbsolutePath() + "/sharable/");
                directory.mkdirs();

                // The file of the sound to be shared
                final File file = new File(directory, fileName);

                // Define an InputStream that will read the data from your sound-raw.mp3 file into a buffer
                InputStream in = view.getContext().getResources().openRawResource(mAdapter.getItem(i).getID());

                try  {

                    // Define an OutputStream/FileOutputStream that will write the buffer data into the sound.mp3 on the external storage
                    OutputStream out = new FileOutputStream(file);

                    // Define a buffer of 1kb
                    byte[] buffer = new byte[1024];
                    int len;

                    // Write the data to the sound.mp3 file while reading it from the sound-raw.mp3
                    // if (int) InputStream.read() returns -1 stream is at the end of file
                    while((len = in.read(buffer, 0, buffer.length)) != -1)  {

                        out.write(buffer, 0 , len);
                    }

                    in.close();
                    out.close();

                } catch(IOException e)  {

                    // Log error if process failed
                    Log.e(TAG, "Failed to save file: " + e.getMessage());
                }

                // Share the streamed sound by intent
                final String AUTHORITY = view.getContext().getPackageName() + ".fileprovider";
                Uri contentUri = FileProvider.getUriForFile(view.getContext(), AUTHORITY, file);

                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);

                // Define the intent to be of type audio/mp3
                intent.setType("audio/mp3");

                // Start a new chooser dialog where the user can choose an app to share the sound
                // TODO: Remove hardcoded intent chooser heading
                view.getContext().startActivity(Intent.createChooser(intent, "Share sound via..."));

                return true;
            }
        });
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

            }
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

    private String convertNameToRaw(String soundName)  {

        return soundName.toLowerCase().replace(" ", "_");
    }

    public abstract View inflateFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
