package co.ryancasler.cpsmart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
    public static final int min = 60000;
    public static final int pulse = 200;
    public static final String I_BPM = "bpmzz";

    private Vibrator vb;
    private boolean running = true;

    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
    private boolean mResolvingError=false;

    @Bind(R.id.hart) ImageView hart;
    @Bind(R.id.label) TextView label;


    private Animation pulseAnimation;

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

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        hart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    vb.cancel();
                    pulseAnimation.cancel();
                }
                else {
                    vb.vibrate(vibrator, -1);
                    pulseAnimation.start();
                }
                running = (!running);
            }
        });

        pulseAnimation.setDuration(delay + 200);
        hart.startAnimation(pulseAnimation);

        vb = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(vibrator, -1);

        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        sendMessage();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        // stop animations and make the hart black
        pulseAnimation.cancel();
        hart.setImageDrawable(getResources().getDrawable(R.drawable.hart_dark, null));
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        // restart animations and turn hart red
        pulseAnimation.start();
        hart.setImageDrawable(getResources().getDrawable(R.drawable.hart, null));
    }

    @Override
    protected void onStop() {
        super.onStop();
        vb.cancel();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*
     * Resolve the node = the connected device to send the message to
     */
    private void resolveNode() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }

    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {
        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD_WEAR_PATH, null).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            //Improve your code
        }
    }


}
