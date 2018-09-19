// Generated code from Butter Knife. Do not modify!
package com.pencil.pencil.businessbook.Business.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.pencil.pencil.businessbook.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ForgetPasswordActivity_ViewBinding implements Unbinder {
  private ForgetPasswordActivity target;

  @UiThread
  public ForgetPasswordActivity_ViewBinding(ForgetPasswordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ForgetPasswordActivity_ViewBinding(ForgetPasswordActivity target, View source) {
    this.target = target;

    target.mEmailET = Utils.findRequiredViewAsType(source, R.id.emailET, "field 'mEmailET'", EditText.class);
    target.mSendBtn = Utils.findRequiredViewAsType(source, R.id.send, "field 'mSendBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ForgetPasswordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mEmailET = null;
    target.mSendBtn = null;
  }
}
