package com.hht.uc.FileBrowser;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.hht.skin.main.SkinManager;
import com.hht.widget.StorageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyj on 18-4-24.
 */

public class StorageItemGroup {
    private final String TAG = "StorageItemGroup";
    private Context mContext;
    private SkinManager mSkinManager;
    private List<StorageItem> mStorageList;
    private ClickListener mClickListener;
    private KeyListener mKeyListener;
    private int mCurrentSelectPosition = -1;
    private boolean bFirstFocus = true;
    private boolean bItemLayoutFocus = false;


    public StorageItemGroup(Context context) {
        mContext = context;
        mSkinManager = SkinManager.getInstance();
        mClickListener = new ClickListener();
        mKeyListener = new KeyListener();
        mStorageList = new ArrayList<>();
        mStorageList.clear();
    }

    public View addItem(int drawableResId, String title, String storagePath) {
        final StorageItem button = new StorageItem(mContext, storagePath);
        final View layout = button.getRootView();
        mStorageList.add(button);
        button.setImage(drawableResId);
        button.setTitle(title);
        button.setSelected(true);
        if (mCurrentSelectPosition >= 0) {
            mStorageList.get(mCurrentSelectPosition).setSelected(false);
        }
        mCurrentSelectPosition++;
        button.setTag(mCurrentSelectPosition);
        button.setOnClickListener(mClickListener);
        button.setOnKeyListener(mKeyListener);
        mSkinManager.injectSkin(layout);
        return layout;
    }

    public void itemRequestFocus(int position) {
        mCurrentSelectPosition = position;
        for (StorageItem item: mStorageList) {
            item.setSelected(false);
        }
        StorageItem selected = mStorageList.get(position);
        selected.setSelected(true);
        selected.requestFocus();
    }

    /**
     * 当焦点从其他地方转移到设备栏时，强制把焦点跳到被选中的设备按钮上
     */
    public boolean forceItemSelectFocus() {
        if (!bItemLayoutFocus) {
            bItemLayoutFocus = true;
            /**
             * 初始化时的焦点背景为透明，首次显示焦点时需要设置焦点背景
             */
            if (bFirstFocus) {
                bFirstFocus = false;
                StorageItem item;
                for (int i = 0; i < mStorageList.size(); i++) {
                    item = mStorageList.get(i);
                    item.readyFocusBackground();
                    if (item.isSelected()) {
                        mCurrentSelectPosition = i;
                        item.requestFocus();
                    }
                }
            } else {
                mStorageList.get(mCurrentSelectPosition).requestFocus();
            }
            return true;
        }
        return false;
    }

    public void lastItemFocus() {
        int last = mStorageList.size() - 1;
        mStorageList.get(last).requestFocus();
    }

    public void layoutFocusStatus(boolean hasFocused) {
        bItemLayoutFocus = hasFocused;
    }

    public boolean isFirstFocus() {
        return bFirstFocus;
    }

    public void clear() {
        mStorageList.clear();
        mCurrentSelectPosition = -1;
        bFirstFocus = true;
        bItemLayoutFocus = false;
    }

    public void requestFocusQuietly() {
        lastItemFocus();
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int clickPosition = (int) v.getTag();
            setItemSelected(mCurrentSelectPosition, false);
            StorageItem selectedItem = setItemSelected(clickPosition, true);
            mCurrentSelectPosition = clickPosition;
            sendUsbNotification(selectedItem.getPath());
        }

        private StorageItem setItemSelected(int position, boolean selected) {
            StorageItem item = mStorageList.get(position);
            item.setSelected(selected);
            return item;
        }

        private void sendUsbNotification(String path) {
            Intent intent = new Intent();
            intent.setAction(FFileMainActivity.ACTION_USB_LIST);
            intent.putExtra("usb_path", path);
            Log.d("wsldwo","mStorageView,  usbPath:" + path);
            mContext.sendBroadcast(intent);
        }
    }

    private class KeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (!bFirstFocus || (event.getAction() != KeyEvent.ACTION_DOWN)) {
                return false;
            }

            /**
             * 第一次显示时，被选中的设备按钮会被最先获取到焦点，因为初始化时不显示焦点框，
             * 因此首次按下方向键后，焦点先固定在被选中的设备按钮，然后显示焦点背景
             */
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    forceItemSelectFocus();
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
}
