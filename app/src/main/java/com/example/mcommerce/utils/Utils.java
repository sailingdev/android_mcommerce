package com.example.mcommerce.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {
	
	public static boolean isConnected(Context context) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			return networkInfo != null && networkInfo.isConnected();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static String loadJSONFromAsset(Context ctx, String filename) throws IOException {
    	String json = null;
        try {
            InputStream is = ctx.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
	
	public static void showShortMessage(Context ctx, String sMsg){
    	Toast.makeText(ctx, sMsg, Toast.LENGTH_SHORT).show();
    }
	
	public static void CreateWorkDirectories(String sPath, boolean bDelOldFiles){
    	File file = new File(sPath);
    	File[] children = file.listFiles();
		if (children == null) {
			file.mkdir();
		} else if (bDelOldFiles){
			Calendar today = Calendar.getInstance();		// Get today as a Calendar
	        today.add(Calendar.DATE, -7);  			// Subtract 1 day
	        long _7DaysAgo = today.getTimeInMillis();  
	        
			for (int i=0; i<children.length; i++){
				if (children[i].lastModified() < _7DaysAgo) {
					//Log.e(GlobalConstant.TAG, children[i].getAbsolutePath() + ", " + children[i].getName());
					children[i].delete();
				}
			}
		}
    }
	
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {

        // can post image
        String[] proj={MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery( contentUri,
                        proj, // Which columns to return
                        null,       // WHERE clause; which rows to return (all rows)
                        null,       // WHERE clause selection arguments (none)
                        null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
	}
	
	public synchronized static int GetExifOrientation(String filepath) 	{
	    int degree = 0;
	    ExifInterface exif = null;
	    
	    try    {
	        exif = new ExifInterface(filepath);
	    } catch (IOException e)  {
	        Log.e("StylePhoto", "cannot read exif");
	        e.printStackTrace();
	    }
	    
	    if (exif != null) {
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	        
	        if (orientation != -1) {
	            // We only recognize a subset of orientation tag values.
	            switch(orientation) {
	                case ExifInterface.ORIENTATION_ROTATE_90:
	                    degree = 90;
	                    break;
	                    
	                case ExifInterface.ORIENTATION_ROTATE_180:
	                    degree = 180;
	                    break;
	                    
	                case ExifInterface.ORIENTATION_ROTATE_270:
	                    degree = 270;
	                    break;
	            }
	        }
	    }
	    
	    return degree;
	}
	
	public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) 	{
	    if ( degrees != 0 && bitmap != null )     {
	        Matrix m = new Matrix();
	        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
	        try {
	            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
	            if (bitmap != b2) {
	            	bitmap.recycle();
	            	bitmap = b2;
	            }
	        } catch (OutOfMemoryError ex) {
	            // We have no memory to rotate. Return the original bitmap.
	        }
	    }
	    
	    return bitmap;
	}
	
	@SuppressWarnings("deprecation")
	public synchronized static Bitmap getSafeDecodeBitmap(String strFilePath, int maxSize) {
		try {
			if (strFilePath == null)
				return null;
			// Max image size
			int IMAGE_MAX_SIZE = maxSize;
			
	    	File file = new File(strFilePath);
	    	if (file.exists() == false) {
	    		//DEBUG.SHOW_ERROR(TAG, "[ImageDownloader] SafeDecodeBitmapFile : File does not exist !!");
	    		return null;
	    	}
	    	
	    	BitmapFactory.Options bfo 	= new BitmapFactory.Options();
	    	bfo.inJustDecodeBounds 		= true;
	    	
			BitmapFactory.decodeFile(strFilePath, bfo);
	        
			if (IMAGE_MAX_SIZE > 0) 
		        if(bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
		        	bfo.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE
		        						/ (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
		        }
	        bfo.inJustDecodeBounds = false;
	        bfo.inPurgeable = true;
	        bfo.inDither = true;
	        
	        final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);
	    	
	        int degree = GetExifOrientation(strFilePath);
	        
	    	return GetRotatedBitmap(bitmap, degree);
		}
		catch(OutOfMemoryError ex)
		{
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static Drawable getImageDrawableFromAssetFile(Context context, String assetPath){
		try 
		{
		    InputStream ims = context.getAssets().open(assetPath);
		    Drawable drawable = Drawable.createFromStream(ims, null);
		    return drawable;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final String getFormattedString(int number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
	
	// CONVERT HTTP STREAM TO STRING //
	public static String convertStreamToString(InputStream is, String strCharSet) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,strCharSet));
	        
	        String line = null;
	        
                while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally {
                try { is.close(); }
                catch (IOException e) { e.printStackTrace(); }
        }
        return sb.toString();
	}
	
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	
	public static void showKeyboard(Context ctx, boolean bShow, EditText edtBox){
		if (bShow){
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(edtBox, InputMethodManager.SHOW_IMPLICIT);
		}else{
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtBox.getWindowToken(), 0);
		}
	}
	
	public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
 
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
 
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
 
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
 
        // Set the component to be explicit
        explicitIntent.setComponent(component);
 
        return explicitIntent;
    }
	
	/**
	 * returns true if AlwaysFinishActivities option is enabled/checked
	 */
	@SuppressLint("InlinedApi")
	public static boolean isAlwaysFinishActivitiesOptionEnabled(Context appContext) {
	    int alwaysFinishActivitiesInt = 0;
	    if (Build.VERSION.SDK_INT >= 17) {
	        alwaysFinishActivitiesInt = Settings.System.getInt(appContext.getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
	    } else {
	        alwaysFinishActivitiesInt = Settings.System.getInt(appContext.getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
	    }

	    if (alwaysFinishActivitiesInt == 1) {
	        return true;
	    } else {
	        return false;
	    }
	}
}
