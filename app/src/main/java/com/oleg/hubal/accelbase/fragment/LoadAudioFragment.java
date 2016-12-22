package com.oleg.hubal.accelbase.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oleg.hubal.accelbase.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 22.12.2016.
 */

public class LoadAudioFragment extends Fragment {

    private static final String KEY_PREF_AUDIO_PATH = "AUDIO_PATH";
    private static final String KEY_PREF_SESSION_URI = "SESSION_URI";

    private static final String TAG = "LoadAudioFragment";

    private boolean isSaved = false;

    private ProgressBar mProgressBar;
    private UploadTask mUploadTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load_audio, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading_audio);

        SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
        String audioPath = sPref.getString(KEY_PREF_AUDIO_PATH, "");
        String sessionString = sPref.getString(KEY_PREF_SESSION_URI, "");

        if (!TextUtils.isEmpty(audioPath) && !TextUtils.isEmpty(sessionString)) {
            Uri audioUri = Uri.parse(audioPath);
            Uri sessionUri = Uri.parse(sessionString);
            loadAudio(audioUri, sessionUri);
        } else {
            getAudioFile();
        }

        return view;
    }

    private void getAudioFile() {
        Intent intentAudio = new Intent();
        intentAudio.setType("audio/*");
        intentAudio.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentAudio, 1);
    }

    private void loadAudio(Uri audioUri, Uri sessionUri) {
        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_PREF_AUDIO_PATH, audioUri.toString());
        editor.apply();

        File file = new File(audioUri.getPath());
        String fileName = file.getName();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference audioReference = storageReference.child("audio").child(uId).child(fileName + ".mp3");

        if (TextUtils.isEmpty(sessionUri.toString())) {
            mUploadTask = audioReference.putFile(audioUri, metadata);
        } else {
            mUploadTask = audioReference.putFile(audioUri, metadata, sessionUri);
        }
        mUploadTask.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                }).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        isSaved = true;
                        Log.d(TAG, "onSuccess: ");
                        mProgressBar.setVisibility(View.GONE);
                        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(KEY_PREF_AUDIO_PATH, "");
                        editor.putString(KEY_PREF_SESSION_URI, "");
                        editor.apply();
                    }
                }).addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri sessionUri = taskSnapshot.getUploadSessionUri();
                        Log.d(TAG, "onProgress: " + sessionUri);
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: " + progress);
                        if (sessionUri != null) {
                            if (getActivity() != null) {
                                SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(KEY_PREF_SESSION_URI, sessionUri.toString());
                                editor.apply();
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            mUploadTask.pause();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                loadAudio(uri, Uri.EMPTY);
            }
        }
    }
}
