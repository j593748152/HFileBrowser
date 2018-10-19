package com.hht.filebrowser.processor;

import android.view.View;
import android.widget.GridView;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class CacheColorHintProcessor extends ISkinProcessor {
    private static String CACHE_COLOR_HINT = "cache_color_hint";
    @Override
    public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
        
        if (view instanceof GridView) {
            final int hintColor;
            try {
                hintColor = resourceManager.getColor(resName);
                if (!"".equals(hintColor)) {
                    final GridView gridview = (GridView) view;
                    if (isInUiThread) {
                        gridview.setCacheColorHint(hintColor);
                    } else {
                        UITaskManager.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                gridview.setCacheColorHint(hintColor);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return CACHE_COLOR_HINT;
    }

}
