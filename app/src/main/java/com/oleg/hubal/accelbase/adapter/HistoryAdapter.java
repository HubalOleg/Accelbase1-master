package com.oleg.hubal.accelbase.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.listener.OnHistoryItemClickListener;
import com.oleg.hubal.accelbase.model.Coordinates;
import com.oleg.hubal.accelbase.utility.Constants;
import com.oleg.hubal.accelbase.utility.Utility;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by User on 01.11.2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
        implements View.OnClickListener {

    private TreeMap<String, ArrayList<Coordinates>> mHistoryMap;
    private OnHistoryItemClickListener mItemClickListener;
    private ArrayList<String> mDateKeys;

    public HistoryAdapter(OnHistoryItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
        mHistoryMap = new TreeMap<>();
        mDateKeys = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_history, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StringBuilder coordinatesInfo = new StringBuilder();
        String dateKey = mDateKeys.get(position);

        ArrayList<Coordinates> coordinatesList = mHistoryMap.get(dateKey);

        for (Coordinates coordinates : coordinatesList) {
            String date = Utility.formatDate(coordinates.getDate(), Constants.DATE_HOUR_FORMAT);

            coordinatesInfo.append(date).append("\n")
                    .append(Constants.X).append(coordinates.getCoordinateX()).append("\n")
                    .append(Constants.Y).append(coordinates.getCoordinateY()).append("\n")
                    .append(Constants.Z).append(coordinates.getCoordinateZ()).append("\n");
        }

        holder.tvCoordinateList.setText(coordinatesInfo.toString().trim());
        holder.tvDate.setText(Utility.formatDate(dateKey));
        holder.view.setTag(dateKey);
        holder.view.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mHistoryMap.size();
    }

    public void notifyDataChange(TreeMap<String, ArrayList<Coordinates>> historyMap) {
        mHistoryMap = historyMap;
        mDateKeys = new ArrayList<>(mHistoryMap.keySet());
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onHistoryItemClick((String) v.getTag());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvCoordinateList;
        public View view;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvDate = (TextView) itemView.findViewById(R.id.tvHistoryDate);
            tvCoordinateList = (TextView) itemView.findViewById(R.id.tvCoordinateHistory);
        }
    }
}
