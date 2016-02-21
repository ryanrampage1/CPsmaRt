package co.ryancasler.cpsmart;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ryancasler on 2/20/16.
 */
public class WearableListItemLayout extends LinearLayout implements WearableListView.OnCenterProximityListener {

    private ImageView circle;
    private TextView name;

    private final float fadedTextAlpha;
    private final int fadedCircleColor;
    private final int chosenCircleColor;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        fadedTextAlpha = getResources().getInteger(R.integer.action_text_faded_alpha) / 100f;
        fadedCircleColor = getResources().getColor(android.R.color.transparent);
        chosenCircleColor = getResources().getColor(R.color.cpr_red);
    }

    // Get references to the icon and text in the item layout definition
    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        circle = (ImageView) findViewById(R.id.circle);
        name = (TextView) findViewById(R.id.name);
    }

    @Override public void onCenterPosition(boolean animate) {
        name.setAlpha(1f);
        ((GradientDrawable) circle.getDrawable()).setColor(chosenCircleColor);
    }

    @Override public void onNonCenterPosition(boolean animate) {
        ((GradientDrawable) circle.getDrawable()).setColor(fadedCircleColor);
        name.setAlpha(fadedTextAlpha);
    }
}