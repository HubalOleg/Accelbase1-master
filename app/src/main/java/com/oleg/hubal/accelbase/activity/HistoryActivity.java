package com.oleg.hubal.accelbase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.fragment.DiagramFragment;
import com.oleg.hubal.accelbase.fragment.HistoryFragment;
import com.oleg.hubal.accelbase.listener.OnHistoryItemClickListener;
import com.oleg.hubal.accelbase.model.Coordinates;
import com.oleg.hubal.accelbase.utility.Constants;
import com.oleg.hubal.accelbase.utility.Utility;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by User on 02.11.2016.
 */

public class HistoryActivity extends AppCompatActivity implements OnHistoryItemClickListener {

    private static final String TAG = "HistoryActivity";

    private TreeMap<String, ArrayList<Coordinates>> mCoordinatesHistory;
    private HistoryFragment mHistoryFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mHistoryFrag = new HistoryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragmentContainer, mHistoryFrag).commit();

        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accelerometer:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public void onHistoryItemClick(String historyKey) {
        showDiagramFragment(historyKey);
    }

    private void showDiagramFragment(String historyKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_HISTORY_KEY, historyKey);

        FrameLayout container = (FrameLayout) findViewById(R.id.flDiagramContainerAH);

        if (container == null) {
            Intent intent = new Intent(this, DiagramActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            DiagramFragment diagramFrag = new DiagramFragment();
            diagramFrag.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(container.getId(), diagramFrag).commit();
        }
    }

    private void loadData() {
        Utility.isNetworkConnected(this);

        mCoordinatesHistory = new TreeMap<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot historyItem : dataSnapshot.getChildren()) {
                            ArrayList<Coordinates> coordinatesList = new ArrayList<>();

                            for (DataSnapshot coordinateItem : historyItem.getChildren()) {
                                Coordinates coord = coordinateItem.getValue(Coordinates.class);
                                coordinatesList.add(coord);
                            }
                            mCoordinatesHistory.put(historyItem.getKey(), coordinatesList);
                            mHistoryFrag.notifyDataChange(mCoordinatesHistory);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }
}
