package com.hht.filebrowser.processor;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.EditText;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class TextHintColorProcessor extends ISkinProcessor {
    
    private static String TEXT_HINT_COLOR = "text_hint_color";

	@Override
	public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
		if (view instanceof EditText) {
			final ColorStateList colorList = resourceManager.getColorStateList(resName);
			if (colorList != null) {
				final EditText editView = (EditText) view;
				if (isInUiThread) {
				    editView.setHintTextColor(colorList);
				} else {
					UITaskManager.getInstance().post(new Runnable() {
						@Override
						public void run() {
						    editView.setHintTextColor(colorList);
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return TEXT_HINT_COLOR;
	}
}
