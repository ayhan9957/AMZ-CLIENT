package com.amz.client;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Installs the bundled AMZ native mod into the selected launcher's mod directory. */
public final class AMZNativePackInstaller {
    private static final String TAG = "AMZNativePack";
    private static final String ASSET_NAME = "amz_native.levipack";
    private static final String MOD_ID = "amz_native";

    private AMZNativePackInstaller() {}

    public static boolean install(Context context, File modsDirectory) {
        if (context == null || modsDirectory == null) {
            Log.w(TAG, "Native mod directory is unavailable; skipping bundled pack install");
            return false;
        }

        File target = new File(modsDirectory, MOD_ID);
        File manifest = new File(target, "manifest.json");
        File entry = new File(target, "libamz_native.so");
        if (manifest.isFile() && entry.isFile() && entry.length() > 0) {
            return true;
        }

        File staging = new File(modsDirectory, MOD_ID + ".amz_tmp");
        if (!modsDirectory.exists() && !modsDirectory.mkdirs()) {
            Log.e(TAG, "Cannot create mods directory: " + modsDirectory);
            return false;
        }
        deleteRecursively(staging);
        if (!staging.mkdirs()) {
            Log.e(TAG, "Cannot create staging directory: " + staging);
            return false;
        }

        try (InputStream asset = context.getAssets().open(ASSET_NAME);
             ZipInputStream zip = new ZipInputStream(asset)) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[32768];
            while ((zipEntry = zip.getNextEntry()) != null) {
                String name = zipEntry.getName().replace('\\', '/');
                if (!name.startsWith(MOD_ID + "/")) {
                    zip.closeEntry();
                    continue;
                }
                String relative = name.substring((MOD_ID + "/").length());
                if (relative.isEmpty()) {
                    zip.closeEntry();
                    continue;
                }
                File output = new File(staging, relative);
                String rootPath = staging.getCanonicalPath() + File.separator;
                String outputPath = output.getCanonicalPath();
                if (!outputPath.startsWith(rootPath)) {
                    throw new IOException("Unsafe path in bundled native pack");
                }
                if (zipEntry.isDirectory()) {
                    if (!output.isDirectory() && !output.mkdirs()) {
                        throw new IOException("Cannot create " + output);
                    }
                } else {
                    File parent = output.getParentFile();
                    if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Cannot create " + parent);
                    }
                    try (java.io.FileOutputStream stream = new java.io.FileOutputStream(output)) {
                        int read;
                        while ((read = zip.read(buffer)) != -1) stream.write(buffer, 0, read);
                    }
                }
                zip.closeEntry();
            }

            File stagedManifest = new File(staging, "manifest.json");
            File stagedEntry = new File(staging, "libamz_native.so");
            if (!stagedManifest.isFile() || !stagedEntry.isFile() || stagedEntry.length() == 0) {
                throw new IOException("Bundled AMZ pack is missing manifest or ARM64 library");
            }

            // Preserve per-mod config/data directories across a package replacement.
            for (String runtimeDir : new String[]{"config", "data"}) {
                File oldDir = new File(target, runtimeDir);
                File newDir = new File(staging, runtimeDir);
                if (oldDir.isDirectory() && !newDir.exists()) {
                    copyRecursively(oldDir, newDir);
                }
            }
            deleteRecursively(target);
            try {
                Files.move(staging.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(staging.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            Log.i(TAG, "Bundled AMZ native pack installed in " + target.getAbsolutePath());
            return true;
        } catch (Exception error) {
            Log.e(TAG, "Failed to install bundled AMZ native pack", error);
            deleteRecursively(staging);
            return false;
        }
    }

    private static void copyRecursively(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.isDirectory() && !target.mkdirs()) throw new IOException("Cannot create " + target);
            File[] children = source.listFiles();
            if (children == null) throw new IOException("Cannot list " + source);
            for (File child : children) copyRecursively(child, new File(target, child.getName()));
        } else {
            File parent = target.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) throw new IOException("Cannot create " + parent);
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) for (File child : children) deleteRecursively(child);
        }
        // Best-effort cleanup; installation verifies its output before reporting success.
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
