package edu.teco.dustradarnonegame.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Custom LinearLayout that is used for the BLEBridgeScan fragment
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean mChecked = false;


    // constructors

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    // interface

    public boolean isChecked() {
        return mChecked;
    }


    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }


    public void toggle() {
        setChecked(!mChecked);
    }


    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
