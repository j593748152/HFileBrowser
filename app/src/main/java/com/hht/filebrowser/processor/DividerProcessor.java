package com.hht.filebrowser.processor;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

/**
 * 处理listview的divider
 * 
 * @author hackware 2015.12.26
 * 
 */
public class DividerProcessor extends ISkinProcessor {

    private static String DIVIDER_DRAWABLE = "divider_drawable";
	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof LinearLayout) {
			final Drawable divider = resourceManager.getDrawable(resName);
			if (divider != null) {
				final LinearLayout linearLayout = (LinearLayout) view;
				if (isInUiThread) {
				    linearLayout.setDividerDrawable(divider);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
						    linearLayout.setDividerDrawable(divider);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return DIVIDER_DRAWABLE;
	}
}
