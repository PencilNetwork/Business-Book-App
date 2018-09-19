// Generated code from Butter Knife. Do not modify!
package com.pencil.pencil.businessbook.Business.Fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.pencil.pencil.businessbook.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BProfileFragment_ViewBinding implements Unbinder {
  private BProfileFragment target;

  @UiThread
  public BProfileFragment_ViewBinding(BProfileFragment target, View source) {
    this.target = target;

    target.mOfferRecyclerView = Utils.findRequiredViewAsType(source, R.id.offerRecycleView, "field 'mOfferRecyclerView'", RecyclerView.class);
    target.mRelatedFileRecyclerView = Utils.findRequiredViewAsType(source, R.id.relatedFileRecycleView, "field 'mRelatedFileRecyclerView'", RecyclerView.class);
    target.mUserNameTv = Utils.findRequiredViewAsType(source, R.id.usernameTv, "field 'mUserNameTv'", TextView.class);
    target.mAddressTv = Utils.findRequiredViewAsType(source, R.id.addressTv, "field 'mAddressTv'", TextView.class);
    target.mBusinessNameTv = Utils.findRequiredViewAsType(source, R.id.businessNameTv, "field 'mBusinessNameTv'", TextView.class);
    target.mBusinessDescriptionTv = Utils.findRequiredViewAsType(source, R.id.businessDescriptionTv, "field 'mBusinessDescriptionTv'", TextView.class);
    target.mContactNoTv = Utils.findRequiredViewAsType(source, R.id.contactNumberTv, "field 'mContactNoTv'", TextView.class);
    target.mCategoryTv = Utils.findRequiredViewAsType(source, R.id.categoryTv, "field 'mCategoryTv'", TextView.class);
    target.mEmailTv = Utils.findRequiredViewAsType(source, R.id.emailTv, "field 'mEmailTv'", TextView.class);
    target.mLogoIv = Utils.findRequiredViewAsType(source, R.id.logoIv, "field 'mLogoIv'", ImageView.class);
    target.mBusinessIv = Utils.findRequiredViewAsType(source, R.id.businessIv, "field 'mBusinessIv'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BProfileFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mOfferRecyclerView = null;
    target.mRelatedFileRecyclerView = null;
    target.mUserNameTv = null;
    target.mAddressTv = null;
    target.mBusinessNameTv = null;
    target.mBusinessDescriptionTv = null;
    target.mContactNoTv = null;
    target.mCategoryTv = null;
    target.mEmailTv = null;
    target.mLogoIv = null;
    target.mBusinessIv = null;
  }
}
