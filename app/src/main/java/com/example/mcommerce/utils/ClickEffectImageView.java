package com.example.mcommerce.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ClickEffectImageView extends AppCompatImageView {

	/** 
	 * ImageView for Clicking Effect when Clicking
	 * @author Alex
	 * **/
	
	boolean m_bOutOf = false;
	boolean bTouch = true;
	private Rect rect;    // Variable rect to hold the bounds of the view
	
	public ClickEffectImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setSoundEffectsEnabled(false);
	}

	public ClickEffectImageView(Context context) {
		super(context);
	}
	
	public void setTouchable(boolean bEnable){
		bTouch = bEnable;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!bTouch) return false;
		if (this.getDrawable() == null) return false;
		
		switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            this.getDrawable().setColorFilter(0x20000000,android.graphics.PorterDuff.Mode.SRC_ATOP);
            this.invalidate();
            rect = new Rect(getLeft(), getTop(), getRight(), getBottom());
            m_bOutOf = false;
            return true;
            
        case MotionEvent.ACTION_CANCEL:
            this.getDrawable().clearColorFilter();
            this.invalidate();
            return true;
        case MotionEvent.ACTION_MOVE:
        	if (m_bOutOf)  	return false;
        	
        	if(!rect.contains(getLeft() + (int) event.getX(), getTop() + (int) event.getY())){
        		this.getDrawable().clearColorFilter();
        		this.invalidate();
        		m_bOutOf = true;
        	} else {
        	}
        	return true;
        case MotionEvent.ACTION_UP:
            this.getDrawable().clearColorFilter();
            this.invalidate();
            if (m_bOutOf == false) 
            	performClick();
            return true;
		}
		
		return false;
	}
}
