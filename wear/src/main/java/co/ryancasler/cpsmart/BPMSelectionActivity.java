package co.ryancasler.cpsmart;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;

public class BPMSelectionActivity extends Activity implements WearableListView.ClickListener {

    private String[] elements;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmselection);

        WearableListView listView = (WearableListView) findViewById(R.id.wearable_list);

        elements = getResources().getStringArray(R.array.bpm);

        // setup list adapter and click listener
        listView.setAdapter(new Adapter(this, elements));
        listView.setClickListener(this);
    }

    // WearableListView click listener
    @Override public void onClick(WearableListView.ViewHolder v) {
        // get the selected bpm
        Integer tag = (Integer) v.itemView.getTag();
        String bpms = elements[tag].split(" ")[0];

        // start the next activity
        startActivity(MainActivity.getIntent(this, Integer.parseInt(bpms)));
    }

    @Override public void onTopEmptyRegionClick() {}
}
