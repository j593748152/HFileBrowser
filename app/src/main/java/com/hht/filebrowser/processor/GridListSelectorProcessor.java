package com.hht.filebrowser.processor;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.GridView;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class GridListSelectorProcessor extends ISkinProcessor {
    private static String GRIDLISTSELECTORPROCESSOR = "grid_list_selector";
    @Override
    public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
        
        if (view instanceof GridView) {
            final Drawable drawable = resourceManager.getDrawable(resName);
            if (drawable != null) {
                final GridView gridView = (GridView) view;
                if (isInUiThread) {
                    gridView.setSelector(drawable);
                } else {
                    UITaskManager.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setSelector(drawable);
                        }
                    });
                }
            }
        }
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return GRIDLISTSELECTORPROCESSOR;
    }

}
