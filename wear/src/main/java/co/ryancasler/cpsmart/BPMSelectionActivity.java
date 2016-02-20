package co.ryancasler.cpsmart;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;

public class BPMSelectionActivity extends Activity
        implements WearableListView.ClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmselection);

        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_list);

        // Sample dataset for the list
        String[] elements = { "95 BPM", "100 BPM", "105 BPM", "110 BPM", "115 BPM", "120 BPM" };

        // Assign an adapter to the list
        listView.setAdapter(new Adapter(this, elements));

        // Set a click listener
        listView.setClickListener(this);
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        // use this data to complete some action ...
    }

    @Override
    public void onTopEmptyRegionClick() {
    }
}
