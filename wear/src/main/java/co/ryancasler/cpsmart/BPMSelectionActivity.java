package co.ryancasler.cpsmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import android.widget.Toast;

import java.util.List;

public class BPMSelectionActivity extends Activity implements WearableListView.ClickListener {

    private static final int SPEECH_REQUEST_CODE = 0;

    private String[] elements;
    private String bpms;

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
        if (tag == 0) {
            // first item in the list is voice
            displaySpeechRecognizer();

        } else {
            // get beets per minute and pass it to the pulse activity
            bpms = elements[tag].split(" ")[0];
            startActivity(MainActivity.getIntent(this, Integer.parseInt(bpms)));

        }
    }

    @Override public void onTopEmptyRegionClick() {}

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

//            debug statement
//            Toast.makeText(BPMSelectionActivity.this, spokenText, Toast.LENGTH_SHORT).show();

            // get the bpms part of th result
            try {
                bpms = spokenText.split(" ")[0];
                int beats = Integer.parseInt(bpms);
                if (beats <= 250 && beats >= 50)
                    startActivity(MainActivity.getIntent(this, beats));
                else
                    Toast.makeText(BPMSelectionActivity.this, "Not a valid #", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e){
                Toast.makeText(BPMSelectionActivity.this, "Not a valid #", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
