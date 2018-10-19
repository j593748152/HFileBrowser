package com.hht.filebrowser.processor;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DrawableLeftProcessor extends ISkinProcessor {
    private static String DRAWABLE_LEFT = "drawable_left";
    @Override
    public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {

        if (view instanceof Button) {
            final Drawable drawable = resourceManager.getDrawable(resName);
            if (drawable != null) {
                drawable.setBounds(new Rect(0,0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
                final Button button = (Button) view;
                if (isInUiThread) {
                	button.setCompoundDrawables(drawable, null, null, null);
                } else {
                    UITaskManager.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                        	button.setCompoundDrawables(drawable, null, null, null);
                        }
                    });
                }
            }
        }

    }

    @Override
    public String getName() {
        return DRAWABLE_LEFT;
    }

}
