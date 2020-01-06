package com.vaiha.LemmeShowU.utilities.editText;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class SIEditText
        extends EditText {
    private EditTextImeBackListener mOnImeBack;

    public SIEditText(Context paramContext) {
        super(paramContext);
    }

    public SIEditText(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public SIEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getAction() == 1) && (this.mOnImeBack != null)) {
            this.mOnImeBack.onImeBack(this, getText().toString());
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

    public void setOnEditTextImeBackListener(EditTextImeBackListener paramEditTextImeBackListener) {
        this.mOnImeBack = paramEditTextImeBackListener;
    }
}
