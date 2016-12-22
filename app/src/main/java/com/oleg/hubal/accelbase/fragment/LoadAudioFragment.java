package com.oleg.hubal.accelbase.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 22.12.2016.
 */

public class LoadAudioFragment extends Fragment {

    public static final String SHARED_PREFERENCES = "LOAD_AUDIO_PREF";

    public static final String KEY_PREF_AUDIO_PATH = "AUDIO_PATH";
    public static final String KEY_PREF_SESSION_URI = "SESSION_URI";

    private static final String TAG = "LoadAudioFragment";
    public static final int REQUEST_CODE_AUDIO = 1;

    private ProgressBar mProgressBar;
    private UploadTask mUploadTask;
    private Uri mSessionUri;
    private String mPath;

    private OnSuccessListener<UploadTask.TaskSnapshot> mOnSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            mProgressBar.setVisibility(View.GONE);
            mSessionUri = null;
            saveAudioPath("");
            saveSessionUri(Uri.EMPTY);
        }
    };

    private OnProgressListener<UploadTask.TaskSnapshot> mOnProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            mSessionUri = taskSnapshot.getUploadSessionUri();
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            Log.d(TAG, "onProgress: " + progress);
        }
    };

    private OnFailureListener mOnFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.d(TAG, "onFailure: ");
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load_audio, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading_audio);

        SharedPreferences sPref = getActivity().getApplication().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String audioPath = sPref.getString(KEY_PREF_AUDIO_PATH, "");
        String sessionString = sPref.getString(KEY_PREF_SESSION_URI, "");

        if (!TextUtils.isEmpty(audioPath) && !TextUtils.isEmpty(sessionString)) {
            Uri sessionUri = Uri.parse(sessionString);
            uploadAudio(audioPath, sessionUri);
        } else {
            getAudioFile();
        }

        return view;
    }

    private void getAudioFile() {
        Intent intentAudio = new Intent();
        intentAudio.setType("audio/*");
        intentAudio.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentAudio, REQUEST_CODE_AUDIO);
    }

    private void uploadAudio(String path, Uri sessionUri) {
        mPath = path;
        Uri audioUri = Uri.fromFile(new File(path));

        String fileName = path.substring(path.lastIndexOf("/") + 1);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference audioReference = storageReference.child("audio").child(uId).child(fileName);

        if (TextUtils.isEmpty(sessionUri.toString())) {
            mUploadTask = audioReference.putFile(audioUri, metadata);
        } else {
            mUploadTask = audioReference.putFile(audioUri, metadata, sessionUri);
        }

        mUploadTask.addOnFailureListener(mOnFailureListener)
                .addOnSuccessListener(mOnSuccessListener)
                .addOnProgressListener(mOnProgressListener);
    }

    private String getRealPathFromDocumentUri(Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            return filePath;
        }
        String audioId = m.group();

        String[] column = { MediaStore.Audio.Media.DATA };
        String sel = MediaStore.Audio.Media._ID + "=?";

        Cursor cursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ audioId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    private void saveAudioPath(String path) {
        if (getActivity() != null) {
            SharedPreferences pref = getActivity().getApplication().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_PREF_AUDIO_PATH, path);
            editor.apply();
        }
    }

    private void saveSessionUri(Uri sessionUri) {
        if (getActivity() != null) {
            SharedPreferences pref = getActivity().getApplication().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_PREF_SESSION_URI, sessionUri.toString());
            editor.apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            mUploadTask.pause();
        }
        if (mSessionUri == null) {
            saveSessionUri(Uri.EMPTY);
        } else {
            saveSessionUri(mSessionUri);
            saveAudioPath(mPath);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = data.getData();
                String path = getRealPathFromDocumentUri(audioUri);
                uploadAudio(path, Uri.EMPTY);
            }
        }
    }
}
