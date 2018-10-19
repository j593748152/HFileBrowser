package com.hht.filebrowser.processor;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class CompoundDrawablesProcessor extends ISkinProcessor {
    private static String COMPOUND_DRAWABLES = "compound_drawables";
    @Override
    public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
        
        if (view instanceof Button) {
            final Drawable drawable = resourceManager.getDrawable(resName);
            if (drawable != null) {
                drawable.setBounds(new Rect(0,0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
                final Button button = (Button) view;
                if (isInUiThread) {
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                } else {
                    UITaskManager.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                        }
                    });
                }
            }
        }

    }

    @Override
    public String getName() {
        return COMPOUND_DRAWABLES;
    }

}
