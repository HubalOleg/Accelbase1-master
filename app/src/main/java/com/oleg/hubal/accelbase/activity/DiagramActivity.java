package com.oleg.hubal.accelbase.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.fragment.DiagramFragment;

/**
 * Created by User on 03.11.2016.
 */

public class DiagramActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);

        if (savedInstanceState == null) {
            Fragment diagramFrag = new DiagramFragment();
            diagramFrag.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flDiagramContainerAD, diagramFrag).commit();
        }
    }

}