package com.anhubo.anhubo.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by luoli on 9/27/16
 * 用户检测edittext输入状态，如果有内容，那么显示清空按钮，如果没有内容，则清空
 * <p>
 *     用法:
 *     <br/>edittext.addTextChangedListener(new InputWatcher(btnClear, etContent));
 *     <br/>{@link EditText#addTextChangedListener(TextWatcher)}
 * </p>
 */
public class InputWatcher implements TextWatcher {
    private static final String TAG = "InputWatcher" ;
    private Button mBtnClear;
    private EditText mEtContainer ;

    /**
     *  @param btnClear 清空按钮 可以是button的子类
     * @param etContainer edittext
     */
    public InputWatcher(Button btnClear, EditText etContainer) {
        if (btnClear == null || etContainer == null) {
            //throw new IllegalArgumentException("请确保btnClear和etContainer不为空");
            return;
        }
        this.mBtnClear = btnClear;
        this.mEtContainer = etContainer;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
            if (mBtnClear != null) {
                mBtnClear.setVisibility(View.VISIBLE);
                mBtnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEtContainer != null) {
                            mEtContainer.getText().clear();
                        }
                    }
                });
            }
        } else {
            if (mBtnClear != null) {
                mBtnClear.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}