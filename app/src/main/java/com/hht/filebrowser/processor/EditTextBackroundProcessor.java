package com.hht.filebrowser.processor;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class EditTextBackroundProcessor extends ISkinProcessor {
    private static String EDITTEXTBACKROUND = "edittext_backround";
	@Override
	public void process(final View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
	    if(view instanceof EditText){
	        final Drawable drawable = resourceManager.getDrawable(resName);
	        if (drawable != null) {
	        	final EditText editText = (EditText)view;
	            if (isInUiThread) {
	            	editText.setBackground(drawable);
	            } else {
	                UITaskManager.getInstance().post(new Runnable() {
	                    @Override
	                    public void run() {
	                    	editText.setBackground(drawable);
	                    }
	                });
	            }
	        }
	    }
		
	}

	@Override
	public String getName() {
		return EDITTEXTBACKROUND;
	}
}
