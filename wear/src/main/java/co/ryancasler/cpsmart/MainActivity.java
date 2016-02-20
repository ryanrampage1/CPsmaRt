package co.ryancasler.cpsmart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    private Vibrator vb;
    private boolean running = true;

    @Bind(R.id.hart) ImageView hart;
    @Bind(R.id.label) TextView label;

    public static final int min = 60000;
    public static final int pulse = 200;
    public static final String I_BPM = "bpmzz";

    public static Intent getIntent(Context c, int bpm){
        Intent i = new Intent(c, MainActivity.class);
        i.putExtra(I_BPM, bpm);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        ButterKnife.bind(this);

        // debug toast because the bluetooth debugger suxxxx
//        Toast.makeText(MainActivity.this, Integer.toString(getIntent().getIntExtra(I_BPM, 100)), Toast.LENGTH_SHORT).show();

        // get the bpm the user selected
        int bpm;
        if (getIntent() != null)
            bpm = getIntent().getIntExtra(I_BPM, 100);
        else
            bpm =  100;

        label.setText(Integer.toString(bpm) + " BPM");
        // calculate delay from bpm
        int delay = ( min - ( bpm * pulse) ) / bpm;

        final long[] vibrator = new long[bpm * 2];

        vibrator[0] = 0;

        for (int i = 1; i < bpm; i = i+2 ) {
            vibrator[i] = (long) 200;
            vibrator[i+1] = (long) delay;
        }

        final Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

        hart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    vb.cancel();
                    pulse.cancel();
                }
                else {
                    vb.vibrate(vibrator, -1);
                    pulse.start();
                }
                running = (!running);
            }
        });

        pulse.setDuration(delay + 200);
        hart.startAnimation(pulse);

        vb = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(vibrator, -1);
        
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
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
