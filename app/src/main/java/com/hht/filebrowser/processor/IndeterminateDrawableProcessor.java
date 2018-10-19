package com.hht.filebrowser.processor;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class IndeterminateDrawableProcessor extends ISkinProcessor {
    private static String INDETERMINATE_DRAWABLE = "indeterminate_drawable";
    @Override
    public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
        
        if (view instanceof ProgressBar) {
            final Drawable drawable = resourceManager.getDrawable(resName);
            if (drawable != null) {
                final ProgressBar progressBar = (ProgressBar) view;
                if (isInUiThread) {
                    progressBar.setIndeterminateDrawable(drawable);
                } else {
                    UITaskManager.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setIndeterminateDrawable(drawable);
                        }
                    });
                }
            }
        }
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return INDETERMINATE_DRAWABLE;
    }

}
