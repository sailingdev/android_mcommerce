package com.example.mcommerce.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

@SuppressLint("WrongCall")
public class CircleImageView extends ClickEffectImageView {

	private static final int COMPLETE = 0;
	private static final int FAILED = 1;

	protected Context m_context;
	protected Drawable m_drawable = null;
	protected Bitmap m_bitmap = null;
	protected int m_iNoImageResource=0;
	
	public CircleImageView(final Context context, final AttributeSet attrSet) {
		super(context, attrSet);		
				
	}
	
	
	/**
	 * This is used when creating the view programatically
	 * Once you have instantiated the view you can call
	 * setImageDrawable(url) to change the image
	 * @param context the Activity context
	 */
	public CircleImageView(final Context context) {
		super(context);
		
	}
	

	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest
	 * @param imageUrl the url of the image you wish to load
	 */
	public void setUrlImageDrawable(final String imageUrl) {
	
		new Thread(){
			public void run() {
				try {
					m_drawable = getDrawableFromUrl(imageUrl);
					imageLoadedHandler.sendEmptyMessage(COMPLETE);
				} catch (Exception e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				} 
			};
		}.start();

	}
	int SCREEN_WIDTH = 1;


	public void setOriginUrlImageDrawable1(final String imageUrl, int screenWith) {
		SCREEN_WIDTH = screenWith;
		
		new Thread(){
			public void run() {
				try {
					m_bitmap = GetOriginImageFromURL(imageUrl);
					imageLoadedHandler.sendEmptyMessage(COMPLETE);
				} catch (Exception e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				} 
			};
		}.start();

	}
	
	
	
	/**
	 * Set image resource to be shown if image can not be loaded 
	 * @param iResourceId resource id
	 */
	public void setNoImageResource (int iResourceId) {
		m_iNoImageResource = iResourceId;
	}
	
	/**
	 * Callback that is received once the image has been downloaded
	 */
	private final Handler imageLoadedHandler = new Handler(new Callback() {
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case COMPLETE:
				if (m_bitmap != null){
					setImageBitmap(m_bitmap);
					invalidate();
				}else if (m_drawable != null){
					setImageDrawable(m_drawable);
					invalidate();
				}else{
					if (m_iNoImageResource > 0){
						setImageResource(m_iNoImageResource);
						invalidate();
					}
				}
				break;
			case FAILED:
			default:
				if (m_iNoImageResource > 0)
					setImageResource(m_iNoImageResource);	
				break;
			}
			return true;
		}		
	});

	/**
	 * Pass in an image url to get a drawable object
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		
		return Drawable.createFromStream(((InputStream)new java.net.URL(url).getContent()), "name");
	}

	private Bitmap GetOriginImageFromURL(String strImageURL)
	{
		try{
			BitmapFactory.Options optnsSizeOnly = new BitmapFactory.Options();
			optnsSizeOnly.inJustDecodeBounds = true;
			InputStream inputStreamSizeOnly = (InputStream) new java.net.URL(strImageURL).getContent();
			BitmapFactory.decodeStream(inputStreamSizeOnly, null, optnsSizeOnly);
			int widthOriginal = optnsSizeOnly.outWidth;
			int TARGET_WIDTH = SCREEN_WIDTH -SCREEN_WIDTH/4;
			// Determining the scale ratio.
			// Note, it's just an example, you should use more sophisticated algorithm:
			int ratio = 1;
			if (widthOriginal > TARGET_WIDTH){
				 ratio = widthOriginal / TARGET_WIDTH; // widthView is supposed to be known
			}
			// Now loading the scaled image:
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = ratio;

			InputStream inputStream = (InputStream) new java.net.URL(strImageURL).getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
			return bitmap;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	protected void onDraw1(Canvas canvas) {
	    Path clipPath = new Path();
	    
	    int w = this.getWidth();
	    int h = this.getHeight();
	    float radius = w / 15;
	    float padding = 0;
	    clipPath.addRoundRect(new RectF(padding, padding, w - padding, h - padding), radius, radius, Path.Direction.CW);
	    canvas.clipPath(clipPath);
	    super.onDraw(canvas);
	}
		
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// Round some corners betch!
		int w = this.getWidth();
		int h = this.getHeight();
	    float mCornerRadius = w / 30;

		Drawable maiDrawable = getDrawable();
		if (maiDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
			Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
			
	        final int color = 0xff000000;
	        Rect bitmapBounds = maiDrawable.getBounds();
	        final RectF rectF = new RectF(bitmapBounds);
	        // Create an off-screen bitmap to the PorterDuff alpha blending to work right
			int saveCount = canvas.saveLayer(rectF, null,
                    Canvas.ALL_SAVE_FLAG |
                    Canvas.ALL_SAVE_FLAG |
                    Canvas.ALL_SAVE_FLAG |
                    Canvas.ALL_SAVE_FLAG |
                    Canvas.ALL_SAVE_FLAG);
			// Resize the rounded rect we'll clip by this view's current bounds
			// (super.onDraw() will do something similar with the drawable to draw)
			getImageMatrix().mapRect(rectF);
 
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        //canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
	        	        
	        canvas.drawCircle(w/2, h/2, w/2, paint);
			Xfermode oldMode = paint.getXfermode();
			// This is the paint already associated with the BitmapDrawable that super draws
	        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	        super.onDraw(canvas);
	        paint.setXfermode(oldMode);
	        canvas.restoreToCount(saveCount);
		} else {
			super.onDraw(canvas);
		}
	    
	}
}
