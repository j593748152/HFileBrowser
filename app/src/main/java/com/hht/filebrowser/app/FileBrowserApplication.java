package com.hht.filebrowser.app;

import android.app.Application;

import com.hht.filebrowser.processor.CacheColorHintProcessor;
import com.hht.filebrowser.processor.DividerProcessor;
import com.hht.filebrowser.processor.DrawableLeftProcessor;
import com.hht.filebrowser.processor.DrawableRightProcessor;
import com.hht.filebrowser.processor.EditTextBackroundProcessor;
import com.hht.filebrowser.processor.GridListSelectorProcessor;
import com.hht.filebrowser.processor.IndeterminateDrawableProcessor;
import com.hht.filebrowser.processor.ListScrollbarProcessor;
import com.hht.filebrowser.processor.ListSelectorProcessor;
import com.hht.filebrowser.processor.TextHintColorProcessor;
import com.hht.skin.main.SkinManager;

public class FileBrowserApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager skinManager = SkinManager.getInstance();
        skinManager.init(this);
        skinManager.addProcessor(new CacheColorHintProcessor());
        skinManager.addProcessor(new DividerProcessor());
        skinManager.addProcessor(new DrawableRightProcessor());
        skinManager.addProcessor(new GridListSelectorProcessor());
        skinManager.addProcessor(new IndeterminateDrawableProcessor());
        skinManager.addProcessor(new ListScrollbarProcessor());
        skinManager.addProcessor(new ListSelectorProcessor());
        skinManager.addProcessor(new TextHintColorProcessor());
        skinManager.addProcessor(new EditTextBackroundProcessor());
        skinManager.addProcessor(new DrawableLeftProcessor());
    }
}
