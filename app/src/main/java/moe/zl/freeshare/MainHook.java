package moe.zl.freeshare;

import android.app.AndroidAppHelper;
import android.content.res.XModuleResources;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.graphics.Rect;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import com.google.android.material.card.MaterialCardView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.Throwable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class MainHook implements IXposedHookLoadPackage {

  private String TAG = "Free Launcher";
  private MaterialCardView mCard;

  @Override
  public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

    XModuleResources modRes = XModuleResources.createInstance(lpparam.appInfo.sourceDir, null);
    XposedBridge.log(TAG + ": Found target package: " + lpparam.packageName);
    try {
      XposedHelpers.findAndHookConstructor(
          "com.android.quickstep.views.RecentsView", // 目标类的完整名称
          lpparam.classLoader,
          Context.class,
          AttributeSet.class,
          int.class,
          new XC_MethodHook() { // Hook 逻辑回调
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              Context mContext = (Context) AndroidAppHelper.currentApplication();
              Context oriContext = (Context) param.args[0];
              XposedBridge.log(TAG + "my context " + mContext.toString());
              // Context themedContext = new ContextThemeWrapper(oriContext, themeId);
              CardView mCard = new CardView(mContext);
              mCard.setId(View.generateViewId());
              mCard.setCardBackgroundColor(0xff1f1e33);
              mCard.setContextClickable(true);

              FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(400, 400);
              layoutParams.gravity = Gravity.TOP;
              XposedHelpers.callMethod(param.thisObject, "addView", mCard, layoutParams);
              Toast.makeText(oriContext,TAG + "my Card" + mCard.toString(),1);
            }
          });
      XposedHelpers.findAndHookMethod(
          "com.android.quickstep.AbsSwipeUpHandler", // 目标类的完整名称
          lpparam.classLoader, // 目标应用的类加载器
          "updateSysUiFlags", // 目标方法的名称
          float.class, // 目标方法的参数类型列表 (这里只有一个 float)
          new XC_MethodHook() { // Hook 逻辑回调
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              float progress = (float) param.args[0];
              Object mRecentsView = XposedHelpers.getObjectField(param.thisObject, "mRecentsView");
              if (progress > 3) {
                if (mRecentsView != null) {
                    
                    XposedHelpers.callMethod(param.thisObject,"performHapticFeedback");
                  int[] position = new int[2];
                  Object mTaskView = XposedHelpers.callMethod(mRecentsView, "getRunningTaskView");
                  Object mTaskContainer =
                      XposedHelpers.callMethod(
                          XposedHelpers.callMethod(mTaskView, "getTaskContainers"), "get", 0);
                  XposedHelpers.callMethod(mTaskContainer, "setOverlayEnabled", true);
                  Object mSnapshotView =
                      XposedHelpers.callMethod(mTaskContainer, "getSnapshotView");
                  // XposedHelpers.callMethod(mSnapshotView, "setBackgroundColor",
                  // 0xff1f1e33);
                  XposedHelpers.callMethod(mSnapshotView, "getLocationOnScreen", position);
                  int width =
                      (int)
                          ((int) XposedHelpers.callMethod(mSnapshotView, "getWidth")
                              * (float) XposedHelpers.callMethod(mTaskView, "getScaleX"));
                  int height =
                      (int)
                          ((int) XposedHelpers.callMethod(mSnapshotView, "getHeight")
                              * (float) XposedHelpers.callMethod(mTaskView, "getScaleY"));
                  Rect taskBounds =
                      new Rect(position[0], position[1], position[0] + width, position[1] + height);
                  // XposedBridge.log(TAG + ": " + taskBounds.toString());

                }
              }
              /* String logMessage = "updateDisplacement called with value: " + progress;
              XposedBridge.log(TAG + ": " + logMessage);*/
            }
          });
      XposedHelpers.findAndHookMethod(
          "com.android.quickstep.AbsSwipeUpHandler", // 目标类的完整名称
          lpparam.classLoader, // 目标应用的类加载器
          "onCalculateEndTarget", // 目标方法的名称
          new XC_MethodHook() { // Hook 逻辑回调
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              float progress =
                  (float)
                      XposedHelpers.getObjectField(
                          XposedHelpers.getObjectField(param.thisObject, "mCurrentShift"), "value");
              String logMessage = "end gesture: " + progress;
              XposedBridge.log(TAG + ": " + logMessage);
              Object mRecentsView = XposedHelpers.getObjectField(param.thisObject, "mRecentsView");
              if (progress > 3) {
                if (mRecentsView != null) {
                  int[] position = new int[2];
                  Object HOME_TARGET =
                      XposedHelpers.findClass(
                              "com.android.quickstep.GestureState.GestureEndTarget",
                              lpparam.classLoader)
                          .getEnumConstants()[0];
                  Object mTaskView = XposedHelpers.callMethod(mRecentsView, "getRunningTaskView");
                  Object mTaskContainer =
                      XposedHelpers.callMethod(
                          XposedHelpers.callMethod(mTaskView, "getTaskContainers"), "get", 0);
                  // XposedHelpers.callMethod(mTaskContainer, "setOverlayEnabled", true);
                  Object mSnapshotView =
                      XposedHelpers.callMethod(mTaskContainer, "getSnapshotView");
                  // XposedHelpers.callMethod(mSnapshotView, "setBackgroundColor",
                  // 0xff1f1e33);
                  XposedHelpers.callMethod(mSnapshotView, "getLocationOnScreen", position);
                  int width =
                      (int)
                          ((int) XposedHelpers.callMethod(mSnapshotView, "getWidth")
                              * (float) XposedHelpers.callMethod(mTaskView, "getScaleX"));
                  int height =
                      (int)
                          ((int) XposedHelpers.callMethod(mSnapshotView, "getHeight")
                              * (float) XposedHelpers.callMethod(mTaskView, "getScaleY"));
                  Rect taskBounds =
                      new Rect(position[0], position[1], position[0] + width, position[1] + height);
                  XposedBridge.log(TAG + "Task Bound: " + taskBounds.toString());

                  XposedHelpers.callMethod(
                      XposedHelpers.getObjectField(param.thisObject, "mGestureState"),
                      "setEndTarget",
                      HOME_TARGET);


                  Object task = XposedHelpers.getObjectField(mTaskContainer, "task");
                  Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject,"mContext");
                                int mode = 1;
                                FreeClass.startFreeformByIntent(mContext,task,mode);
                  
                }
              }
            }
          });
    } catch (XposedHelpers.ClassNotFoundError e) {
      XposedBridge.log(TAG + ": Class not found ");
      XposedBridge.log(e);
    } catch (NoSuchMethodError e) {
      XposedBridge.log(TAG + ": Method not found ");
      XposedBridge.log(e);
    } catch (Throwable t) {
      XposedBridge.log(TAG + ": An unexpected error occurred during hooking.");
      XposedBridge.log(t);
    }
  }

  ActivityOptions getOpt(Rect r) {
    ActivityOptions opt = ActivityOptions.makeBasic();

    XposedHelpers.callMethod(opt, "setLaunchWindowingMode", 5);
    XposedHelpers.callMethod(opt, "setTaskAlwaysOnTop", true);
    XposedHelpers.callMethod(opt, "setTaskOverlay", true, true);
    XposedHelpers.callMethod(opt, "setApplyMultipleTaskFlagForShortcut", true);
    XposedHelpers.callMethod(opt, "setApplyActivityFlagsForBubbles", true);
    XposedHelpers.callMethod(opt, "setLaunchedFromBubble", true);
    /* final View decorView = container.getWindow().getDecorView();
    final WindowInsets insets = decorView.getRootWindowInsets();
    r.offsetTo(insets.getSystemWindowInsetLeft() + 50, insets.getSystemWindowInsetTop() + 50);*/
    opt.setLaunchBounds(r);

    return opt;
  }

    
}
