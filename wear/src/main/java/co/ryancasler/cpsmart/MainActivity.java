package co.ryancasler.cpsmart;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private Vibrator vb;

    public static int min = 60000;
    public static int pulse = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        int bpm = 100;
        int delay = ( min - ( bpm * pulse) ) / bpm;

        final long[] vibrator = new long[bpm * 2];

        for (int i = 0; i < bpm; i = i+2 ) {
            vibrator[i] = (long) 200;
            vibrator[i+1] = (long) delay;
        }

        vb = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vb.vibrate(vibrator, -1);
            }
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
//        vb.cancel();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }
}
