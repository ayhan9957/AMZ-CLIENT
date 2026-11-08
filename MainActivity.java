
package com.amz.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LinearLayout root;
    private TextView status;

    private int bg = Color.rgb(13, 15, 25);
    private int panel = Color.rgb(25, 29, 45);
    private int accent = Color.rgb(190, 70, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openMinecraftAutomatically();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setPadding(12, 12, 12, 12);
        return view;
    }

    private Button makeButton(String title, Runnable action) {
        Button button = new Button(this);
        button.setText(title);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setBackground(shape(accent, 22));
        button.setOnClickListener(v -> action.run());

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        55
                );
        params.setMargins(0, 8, 0, 8);
        button.setLayoutParams(params);

        return button;
    }

    private void showHome() {
        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 35, 22, 25);
        root.setBackgroundColor(bg);

        scroll.setBackgroundColor(bg);
        scroll.addView(root);

        TextView logo = text("AMZ CLIENT", 30, Color.WHITE);
        root.addView(logo);

        TextView subtitle = text(
                "MINECRAFT BEDROCK EDITION",
                12,
                accent
        );
        root.addView(subtitle);

        status = text("Durum: Hazır", 14, Color.GREEN);
        root.addView(status);

        addSpace();

        root.addView(makeButton("OYUNU AÇ", this::openMinecraft));

        root.addView(makeButton("CLIENT ÖZELLİKLERİ", this::showFeatures));

        root.addView(makeButton("AYARLAR", this::showSettings));

        root.addView(makeButton("HAKKINDA", this::showAbout));

        TextView footer = text(
                "AMZ CLIENT • Android",
                12,
                Color.GRAY
        );
        root.addView(footer);

        setContentView(scroll);
    }

    private void addSpace() {
        TextView space = new TextView(this);
        space.setHeight(25);
        root.addView(space);
    }

    private void openMinecraftAutomatically() {
        try {
            if (android.provider.Settings.canDrawOverlays(this)) {
                startService(new Intent(this, AMZOverlayService.class));
            } else {
                Toast.makeText(this, "AMZ yuvarlak menüsü için Üstte göster izni gerekli.", Toast.LENGTH_LONG).show();
            }

            Intent intent = getPackageManager()
                    .getLaunchIntentForPackage("com.mojang.minecraftpe");
            if (intent == null) {
                intent = getPackageManager()
                        .getLaunchIntentForPackage("com.mojang.minecraftpf");
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // Keep AMZ Client alive behind Minecraft so its overlay stays available.
            } else {
                showHome();
                new AlertDialog.Builder(this)
                        .setTitle("Minecraft bulunamadı")
                        .setMessage("Minecraft PE veya PF cihazda kurulu değil.")
                        .setPositiveButton("Tamam", null)
                        .show();
            }
        } catch (Exception e) {
            showHome();
            Toast.makeText(this, "Minecraft açılamadı: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openMinecraft() {
        try {
            Intent intent = getPackageManager()
                    .getLaunchIntentForPackage("com.mojang.minecraftpe");

            if (intent == null) {
                intent = getPackageManager()
                        .getLaunchIntentForPackage("com.mojang.minecraftpf");
            }

            if (intent != null) {
                startActivity(intent);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Minecraft bulunamadı")
                        .setMessage(
                                "Minecraft PE veya PF cihazda kurulu değil."
                        )
                        .setPositiveButton("Tamam", null)
                        .show();
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Minecraft açılamadı.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void showFeatures() {
        String[] features = {
                "Arayüz",
                "Görsel efektler",
                "Demo özellik paneli"
        };

        new AlertDialog.Builder(this)
                .setTitle("AMZ Client")
                .setItems(features, (dialog, which) -> {
                    Toast.makeText(
                            this,
                            features[which],
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setPositiveButton("Kapat", null)
                .show();
    }

    private void showSettings() {
        new AlertDialog.Builder(this)
                .setTitle("Ayarlar")
                .setMessage(
                        "AMZ Client arayüz ayarları\n\n" +
                        "Sürüm: 1.0\n" +
                        "Tema: Dark Purple"
                )
                .setPositiveButton("Tamam", null)
                .show();
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle("AMZ CLIENT")
                .setMessage(
                        "AMZ Client Android arayüz prototipi.\n\n" +
                        "Minecraft oyun kodunu değiştirmez."
                )
                .setPositiveButton("Tamam", null)
                .show();
    }
}
