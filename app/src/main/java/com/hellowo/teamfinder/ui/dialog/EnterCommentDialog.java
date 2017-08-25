package com.hellowo.teamfinder.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hellowo.teamfinder.R;

public class EnterCommentDialog extends BottomSheetDialog {
    DialogInterface dialogInterface;

    public void setDialogInterface(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_input_text, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        sheetBehavior = (BottomSheetBehavior)layoutParams.getBehavior();
        if (sheetBehavior != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);

            EditText messageInput = (EditText) contentView.findViewById(R.id.messageInput);
            messageInput.setHint(R.string.enter_comment);

            TextView sendBtn = (TextView) contentView.findViewById(R.id.sendBtn);
            sendBtn.setOnClickListener(v->{
                if(!TextUtils.isEmpty(messageInput.getText())) {
                    dialogInterface.enterText(messageInput.getText().toString());
                }
            });

            messageInput.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(messageInput,
                        InputMethodManager.SHOW_IMPLICIT);
            }, 0);
        }
    }

    public interface DialogInterface{
        void enterText(String text);
    }
}
