package com.amz.client;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.app.AlertDialog;

public final class AMZOverlayPermissionManager {
    private static final String PREFS = "amz_overlay_permissions";
    private static final String USAGE_ASKED = "usage_asked";
    private static final String OVERLAY_ASKED = "overlay_asked";

    private AMZOverlayPermissionManager() {}

    private static boolean hasUsageAccess(Context context) {
        try {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps == null) return false;
            return appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void maybePrompt(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
        android.content.SharedPreferences prefs =
                activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        if (!hasUsageAccess(activity) && !prefs.getBoolean(USAGE_ASKED, false)) {
            new AlertDialog.Builder(activity)
                    .setTitle("AMZ — Kullanım erişimi")
                    .setMessage("Yuvarlak menünün Minecraft dışına çıkınca gizlenmesi için Kullanım erişimini aç.")
                    .setPositiveButton("Ayarları aç", (dialog, which) -> {
                        prefs.edit().putBoolean(USAGE_ASKED, true).apply();
                        try {
                            activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                        } catch (Exception ignored) {}
                    })
                    .setNegativeButton("Daha sonra", (dialog, which) ->
                            prefs.edit().putBoolean(USAGE_ASKED, true).apply())
                    .show();
            return;
        }

        if (!Settings.canDrawOverlays(activity) && !prefs.getBoolean(OVERLAY_ASKED, false)) {
            new AlertDialog.Builder(activity)
                    .setTitle("AMZ — Üstte göster")
                    .setMessage("Minecraft oynarken AMZ yuvarlağını göstermek için bu izni aç.")
                    .setPositiveButton("Ayarları aç", (dialog, which) -> {
                        prefs.edit().putBoolean(OVERLAY_ASKED, true).apply();
                        try {
                            Intent intent = new Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + activity.getPackageName()));
                            activity.startActivity(intent);
                        } catch (Exception ignored) {}
                    })
                    .setNegativeButton("Daha sonra", (dialog, which) ->
                            prefs.edit().putBoolean(OVERLAY_ASKED, true).apply())
                    .show();
        }
    }

    public static void startOverlayIfAllowed(Context context) {
        if (context == null || !hasUsageAccess(context) || !Settings.canDrawOverlays(context)) return;
        try {
            Intent service = new Intent(context, AMZOverlayService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
        } catch (Exception ignored) {
            // The launcher remains usable even if Android blocks the optional overlay service.
        }
    }
}
