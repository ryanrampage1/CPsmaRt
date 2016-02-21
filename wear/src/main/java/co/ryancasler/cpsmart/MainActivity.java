package co.ryancasler.cpsmart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends WearableActivity {
    public static final int MINUTE = 60000;
    public static final int PULSE = 200;
    public static final int COMPRESSIONZ_PER_BREATH = 10;
    public static final int BREATH_TIME = 1000;
    public static final int BREATHS_PER_CYCLE = 2;
    public static final int LOOPZ = 10;
    public static final String I_BPM = "bpmzz";

    private Vibrator vb;
    private boolean running = true;
    private long[] vibrator;

    @Bind(R.id.hart) ImageView hart;
    @Bind(R.id.label) TextView label;

    private Animation pulseAnimation;

    public static Intent getIntent(Context c, int bpm) {
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
            bpm = 100;

        label.setText(Integer.toString(bpm) + " BPM");
        // calculate delay from bpm
        int delay = (MINUTE - (bpm * PULSE)) / bpm;

        int size = COMPRESSIONZ_PER_BREATH * 2  // 1 for vibrate and 1 for delay every vibrate
                + (2 * BREATHS_PER_CYCLE)       // rescue breath / delay
                + 1;                            // initial delay

         vibrator = new long[size * LOOPZ];                // initial delay

        vibrator[0] = 0; // slight pause for animation to catch up
        for (int x = 0; x < LOOPZ; x++){
            int disp = + size * LOOPZ * x;
            // add compression stuff
            for (int i = 1 + disp; i < COMPRESSIONZ_PER_BREATH * 2 + 1; i = i + 2) {
                vibrator[i] = (long) PULSE;
                vibrator[i+1] = (long) delay;
            }

            for (int i = COMPRESSIONZ_PER_BREATH * 2 + 1 + disp; i < size; i = i+2){
                // add vibrate stuff
                vibrator[i] = (long) BREATH_TIME;
                vibrator[i+1] = (long) BREATH_TIME*2;
            }
        }

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        hart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    vb.cancel();
                    pulseAnimation.cancel();
                } else {
                    pulseAnimation.start();
                    vb.vibrate(vibrator, 0);
                }
                running = (!running);
            }
        });

        pulseAnimation.setDuration((delay + 200) / 2);
        hart.startAnimation(pulseAnimation);

        vb = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(vibrator, 0);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        // stop animations and make the hart black
        pulseAnimation.cancel();
        hart.setColorFilter(Color.parseColor("#0f0f0f"), PorterDuff.Mode.DARKEN);
        vb.vibrate(vibrator, 0);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        vb.vibrate(vibrator, 0);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        // restart animations and turn hart red
        pulseAnimation.start();
        hart.setColorFilter(null);
        vb.vibrate(vibrator, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vb.cancel();
    }
}
