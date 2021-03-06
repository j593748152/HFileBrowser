package com.hht.filebrowser.processor;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListView;

import com.hht.skin.common.ResourceManager;
import com.hht.skin.common.UITaskManager;
import com.hht.skin.processor.ISkinProcessor;

public class ListSelectorProcessor extends ISkinProcessor {
    private static String LISTSELECTORPROCESSOR = "list_selector";
    @Override
    public void process(View view, String resName, ResourceManager resourceManager, boolean isInUiThread) {
        
        if (view instanceof ListView) {
            final Drawable drawable = resourceManager.getDrawable(resName);
            if (drawable != null) {
                final ListView listView = (ListView) view;
                if (isInUiThread) {
                    listView.setSelector(drawable);
                } else {
                    UITaskManager.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelector(drawable);
                        }
                    });
                }
            }
        }
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return LISTSELECTORPROCESSOR;
    }

}
