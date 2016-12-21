package com.oleg.hubal.accelbase.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.utility.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 02.11.2016.
 */

public class DiagramFragment extends Fragment {

    private final long ONE_MEGABYTE = 1024 * 1024;

    private static final String TAG = "DiagramFragment";

    private ImageView mCoordinatesDiagramImageView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diagram, container, false);

        String mHistoryKey = getArguments().getString(Constants.BUNDLE_HISTORY_KEY);

        mCoordinatesDiagramImageView = (ImageView) view.findViewById(R.id.iv_coordinate_diagram);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("images").child(mHistoryKey + ".jpg");

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri).into(mCoordinatesDiagramImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        return view;
    }
}
