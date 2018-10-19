package com.hht.filebrowser.dialog;

import com.hht.uc.FileBrowser.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by DK-dong on 2015/1/12.
 */
public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context, String strMessage) {
        this(context, R.style.CustomProgressDialog, strMessage);
    }

    public CustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.layout_progress_dialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus) {
            dismiss();
        }
    }
}
