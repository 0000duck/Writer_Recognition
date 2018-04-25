/*
 * Copyright (C) 2009 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cn.hxc.imgrecognition;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * A layout which handles the preview aspect ratio.
 */
public class PreviewFrameLayout extends RelativeLayout {
  /** A callback to be invoked when the preview frame's size changes. */
  private Context context = null;

  public interface OnSizeChangedListener {
    public void onSizeChanged();
  }

  private double mAspectRatio = 4.0 / 3.0;

  public PreviewFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  public void setAspectRatio(double ratio) {
    if (ratio <= 0.0)
      throw new IllegalArgumentException();
    if (mAspectRatio != ratio) {
      mAspectRatio = ratio;
      requestLayout();
    }
  }

  public void showBorder(boolean enabled) {
    setActivated(enabled);
  }
}
