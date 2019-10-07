package com.sinichi.parentingcontrolv3.interfaces;

import android.app.Activity;
import android.content.Context;

public interface splash {

    public void setToFullScreenActivity(Activity activity);

    public void makeSplash(Activity activity, Context first, Class<?> next);
}
