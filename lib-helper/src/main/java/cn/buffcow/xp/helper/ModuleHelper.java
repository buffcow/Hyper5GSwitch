package cn.buffcow.xp.helper;

import static cn.buffcow.xp.helper.XposedHelpers.log;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.HashSet;

import cn.buffcow.xp.helper.HookerClassHelper.CustomMethodUnhooker;
import cn.buffcow.xp.helper.HookerClassHelper.MethodHook;
import io.github.libxposed.api.XposedModuleInterface;

/**
 * @author qingyu
 * <p>Create on 2025/10/09 16:08</p>
 */
public final class ModuleHelper {
    public static final String NOT_EXIST_SYMBOL = "ObjectFieldNotExist";

    @SuppressLint("StaticFieldLeak")
    private static Context mModuleContext = null;

    static HashSet<PreferenceObserver> prefObservers = new HashSet<>();

    static Class<?> ActivityThreadClass;

    static {
        ActivityThreadClass = null;
    }

    public static void printCallStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : stackTrace)
            if (el != null) {
                log(el.getClassName() + " $$ " + el.getMethodName());
            }
    }

    public static CustomMethodUnhooker hookMethod(Method method, MethodHook callback) {
        try {
            return XposedHelpers.doHookMethod(method, callback);
        } catch (Throwable t) {
            log("Failed to hook " + method.getName() + " method");
            log(t);
            return null;
        }
    }

    public static CustomMethodUnhooker findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            log("Failed to hook " + methodName + " method in " + className);
            log(t);
            return null;
        }
    }

    /**
     * Calls an instance or static method of the given object silently.
     *
     * @param obj        The object instance. A class reference is not sufficient!
     * @param methodName The method name.
     * @param args       The arguments for the method call.
     */
    public static Object callMethodSilently(Object obj, String methodName, Object... args) {
        try {
            return XposedHelpers.callMethod(obj, methodName, args);
        } catch (Throwable e) {
            log(e);
            return NOT_EXIST_SYMBOL;
        }
    }

    public static CustomMethodUnhooker findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            log("Failed to hook " + methodName + " method in " + clazz.getCanonicalName());
            log(t);
            return null;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean findAndHookMethodSilently(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean findAndHookMethodSilently(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static CustomMethodUnhooker findAndHookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookConstructor(className, classLoader, parameterTypesAndCallback);
        } catch (Throwable t) {
            log("Failed to hook constructor in " + className);
            log(t);
            return null;
        }
    }

    public static void hookAllConstructors(String className, ClassLoader classLoader, MethodHook callback) {
        try {
            Class<?> hookClass = XposedHelpers.findClassIfExists(className, classLoader);
            if (hookClass == null || XposedHelpers.hookAllConstructors(hookClass, callback).isEmpty())
                log("Failed to hook " + className + " constructor");
        } catch (Throwable t) {
            log(t);
        }
    }

    public static void hookAllConstructors(Class<?> hookClass, MethodHook callback) {
        try {
            if (XposedHelpers.hookAllConstructors(hookClass, callback).isEmpty())
                log("Failed to hook " + hookClass.getCanonicalName() + " constructor");
        } catch (Throwable t) {
            log(t);
        }
    }

    public static void hookAllMethods(String className, ClassLoader classLoader, String methodName, MethodHook callback) {
        try {
            Class<?> hookClass = XposedHelpers.findClassIfExists(className, classLoader);
            if (hookClass == null || XposedHelpers.hookAllMethods(hookClass, methodName, callback).isEmpty())
                log("Failed to hook " + methodName + " method in " + className);
        } catch (Throwable t) {
            log(t);
        }
    }

    public static void hookAllMethods(Class<?> hookClass, String methodName, MethodHook callback) {
        try {
            if (XposedHelpers.hookAllMethods(hookClass, methodName, callback).isEmpty())
                log("Failed to hook " + methodName + " method in " + hookClass.getCanonicalName());
        } catch (Throwable t) {
            log(t);
        }
    }

    public static Object proxySystemProperties(String method, String prop, String val, ClassLoader classLoader) {
        return XposedHelpers.callStaticMethod(XposedHelpers.findClassIfExists("android.os.SystemProperties", classLoader),
                method, prop, val);
    }

    public static Object proxySystemProperties(String method, String prop, int val, ClassLoader classLoader) {
        return XposedHelpers.callStaticMethod(XposedHelpers.findClassIfExists("android.os.SystemProperties", classLoader),
                method, prop, val);
    }

    public static boolean hookAllMethodsSilently(String className, ClassLoader classLoader, String methodName, MethodHook callback) {
        try {
            Class<?> hookClass = XposedHelpers.findClassIfExists(className, classLoader);
            return hookClass != null && !XposedHelpers.hookAllMethods(hookClass, methodName, callback).isEmpty();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean hookAllMethodsSilently(Class<?> hookClass, String methodName, MethodHook callback) {
        try {
            return hookClass != null && !XposedHelpers.hookAllMethods(hookClass, methodName, callback).isEmpty();
        } catch (Throwable t) {
            return false;
        }
    }

    public static Object getStaticObjectFieldSilently(Class<?> clazz, String fieldName) {
        try {
            return XposedHelpers.getStaticObjectField(clazz, fieldName);
        } catch (Throwable t) {
            return NOT_EXIST_SYMBOL;
        }
    }

    public static Object getObjectFieldSilently(Object obj, String fieldName) {
        try {
            return XposedHelpers.getObjectField(obj, fieldName);
        } catch (Throwable t) {
            return NOT_EXIST_SYMBOL;
        }
    }

    public static int getUserId() {
        return (int) XposedHelpers.callStaticMethod(UserHandle.class, "getUserId", Process.myUid());
    }


    public static Context findContext() {
        Context context = null;
        try {
            if (ActivityThreadClass == null) {
                ActivityThreadClass = XposedHelpers.findClass("android.app.ActivityThread", null);
            }
            context = (Application) XposedHelpers.callStaticMethod(ActivityThreadClass, "currentApplication");
            if (context == null) {
                Object currentActivityThread = XposedHelpers.callStaticMethod(ActivityThreadClass, "currentActivityThread");
                if (currentActivityThread != null)
                    context = (Context) XposedHelpers.callMethod(currentActivityThread, "getSystemContext");
            }
        } catch (Throwable ignore) {
        }
        return context;
    }

    public static Context findContext(XposedModuleInterface.PackageLoadedParam lpparam) {
        Context context = null;
        try {
            context = (Application) XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", lpparam.getClassLoader()), "currentApplication");
            if (context == null) {
                Object currentActivityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
                if (currentActivityThread != null)
                    context = (Context) XposedHelpers.callMethod(currentActivityThread, "getSystemContext");
            }
        } catch (Throwable ignore) {
        }
        return context;
    }

    public static String stringifyBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        StringBuilder string = new StringBuilder("Bundle{");
        for (String key : bundle.keySet()) {
            string.append(" ").append(key).append(" -> ").append(bundle.get(key)).append(";");
        }
        string.append(" }Bundle");
        return string.toString();
    }

    public static long getNextMIUIAlarmTime(Context context) {
        long nextTime = 0;
        try {
            nextTime = Settings.Global.getLong(context.getContentResolver(), "next_alarm_clock_long");
        } catch (Settings.SettingNotFoundException e) {
        }
        return nextTime;
    }

    public static void openAppInfo(Context context, String pkg, int user) {
        try {
            Intent intent = new Intent("miui.intent.action.APP_MANAGER_APPLICATION_DETAIL");
            intent.setPackage("com.miui.securitycenter");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.putExtra("package_name", pkg);
            if (user != 0) intent.putExtra("miui.intent.extra.USER_ID", user);
            context.startActivity(intent);
        } catch (Throwable t) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setData(Uri.parse("package:" + pkg));
                if (user != 0)
                    XposedHelpers.callMethod(context, "startActivityAsUser", intent, XposedHelpers.newInstance(UserHandle.class, user));
                else
                    context.startActivity(intent);
            } catch (Throwable t2) {
                log(t2);
            }
        }
    }

    public interface PreferenceObserver {
        void onChange(String key);
    }

    public static void observePreferenceChange(PreferenceObserver prefObserver) {
        prefObservers.add(prefObserver);
    }

    public static void handlePreferenceChanged(@Nullable String key) {
        for (PreferenceObserver prefObserver : prefObservers) {
            prefObserver.onChange(key);
        }
    }

    public static synchronized Context getModuleContext(Context context, String modulePkg) throws Throwable {
        return getModuleContext(context, modulePkg, null);
    }

    public static synchronized Context getModuleContext(Context context, String modulePkg, Configuration config) throws Throwable {
        if (mModuleContext == null) {
            mModuleContext = context.createPackageContext(modulePkg, Context.CONTEXT_IGNORE_SECURITY);
        }
        return config == null ? mModuleContext : mModuleContext.createConfigurationContext(config);
    }

    public static synchronized Resources getModuleRes(Context context, String modulePkg) throws Throwable {
        Configuration config = context.getResources().getConfiguration();
        Context moduleContext = getModuleContext(context, modulePkg, config);
        return moduleContext.getResources();
    }

    private static int thermalId = -1;

    public static int getCPUThermalId() {
        if (thermalId != -1) return thermalId;
        for (var i = 2; i < 40; i = i + 2) {
            try {
                RandomAccessFile cpuReader = new RandomAccessFile("/sys/devices/virtual/thermal/thermal_zone" + i + "/type", "r");
                String sensorType = cpuReader.readLine();
                cpuReader.close();
                if (sensorType.startsWith("cpu-") || sensorType.startsWith("cpu_big")) {
                    thermalId = i;
                    break;
                }
            } catch (Throwable ignored) {
            }
        }
        return thermalId;
    }
}
