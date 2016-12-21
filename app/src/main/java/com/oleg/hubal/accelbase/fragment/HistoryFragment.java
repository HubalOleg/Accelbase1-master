package com.oleg.hubal.accelbase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.activity.HistoryActivity;
import com.oleg.hubal.accelbase.adapter.HistoryAdapter;
import com.oleg.hubal.accelbase.model.Coordinates;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by User on 01.11.2016.
 */

public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        launchRecyclerView();

        return view;
    }

    public void notifyDataChange(TreeMap<String, ArrayList<Coordinates>> historyMap) {
        mHistoryAdapter.notifyDataChange(historyMap);
    }

    private void launchRecyclerView() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mHistoryAdapter = new HistoryAdapter((HistoryActivity) getActivity());
        mRecyclerView.setAdapter(mHistoryAdapter);
    }
}
