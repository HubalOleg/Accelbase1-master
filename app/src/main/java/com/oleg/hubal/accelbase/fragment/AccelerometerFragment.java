package com.oleg.hubal.accelbase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.oleg.hubal.accelbase.utility.Constants;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.utility.Utility;
import com.oleg.hubal.accelbase.service.AccelerometerService;

/**
 * Created by User on 01.11.2016.
 */

public class AccelerometerFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    private CheckBox chbStopStart;
    private EditText etDelay;

    private boolean checkBoxState = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        if (savedInstanceState != null) {
            checkBoxState = savedInstanceState.getBoolean(Constants.STATE_CHECKED);
        }

        etDelay = (EditText) view.findViewById(R.id.etDelay);
        chbStopStart = (CheckBox) view.findViewById(R.id.chbStopStartService);
        chbStopStart.setChecked(checkBoxState);
        chbStopStart.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.STATE_CHECKED, chbStopStart.isChecked());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent intent = new Intent(getContext(), AccelerometerService.class);
        if (isChecked) {
            long delay = Utility.getDelayFromEditText(etDelay.getText());
            intent.putExtra(Constants.BUNDLE_EDIT_TEXT_DELAY, delay);
            getActivity().startService(intent);
        } else {
            getActivity().stopService(intent);
        }
    }

}
