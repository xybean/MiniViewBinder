package com.xybean.viewbinder;

import android.app.Activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Author @xybean on 2018/7/4.
 */
public class ViewBinder {

    public static void bind(Activity activity) {

        Class bindingClass = null;
        try {
            bindingClass = Class.forName(activity.getClass().getCanonicalName() + "_ViewBinder");
            //noinspection unchecked
            Constructor constructor = bindingClass.getConstructor(activity.getClass());
            constructor.newInstance(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable t = e.getTargetException();// 获取目标异常
            t.printStackTrace();
        }
    }

}
