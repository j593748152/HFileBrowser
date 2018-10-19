package com.hht.filebrowser.dialog;

import com.hht.uc.FileBrowser.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class DeleteFileDialog extends Dialog {
    private Dialog mDialog;
    private Button mBtnOk;
    private Button mBtnCancel;

    public DeleteFileDialog(Context context, int themeResId) {
        super(context, themeResId);
        mDialog = new Dialog(context, themeResId);
        init();
    }

    public DeleteFileDialog(Context context) {
        super(context);
        mDialog = new Dialog(context);
        init();
    }

    private void init() {
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = 1000;
        params.height = 280;
        mDialog.getWindow().setAttributes(params);
        mDialog.setContentView(R.layout.dialog_layout);
        mBtnOk = (Button) mDialog.findViewById(R.id.btn_ok);
        mBtnCancel = (Button) mDialog.findViewById(R.id.btn_cancel);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void setBtnOkClickListener(View.OnClickListener listener) {
        mBtnOk.setOnClickListener(listener);
    }

    public void setBtnCancelClickListener(View.OnClickListener listener) {
        mBtnCancel.setOnClickListener(listener);
    }

}
