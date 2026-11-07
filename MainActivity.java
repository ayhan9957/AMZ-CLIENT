package com.amz.client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private VideoView video;
    private LinearLayout panel;
    private TextView status;

    private GradientDrawable rounded(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(28);
        if (stroke != 0) d.setStroke(2, stroke);
        return d;
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String caption) {
        Button b = new Button(this);
        b.setText(caption); b.setAllCaps(false); b.setTextColor(Color.WHITE);
        b.setBackground(rounded(0xDD211629, 0xFFFF426D));
        return b;
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        FrameLayout frame = new FrameLayout(this);
        setContentView(frame);

        video = new VideoView(this);
        frame.addView(video, new FrameLayout.LayoutParams(-1, -1));
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.amz_background);
        video.setVideoURI(uri);
        video.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
            video.start();
        });

        View tint = new View(this);
        tint.setBackgroundColor(0x99070012);
        frame.addView(tint, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(24, 36, 24, 24);
        frame.addView(content, new FrameLayout.LayoutParams(-1, -1));

        TextView logo = text("AMZ CLIENT", 32, Color.WHITE, true);
        logo.setGravity(Gravity.CENTER);
        content.addView(logo);
        TextView version = text("BEDROCK EDITION  •  1.21.93", 12, 0xFFFFA9BD, true);
        version.setGravity(Gravity.CENTER);
        content.addView(version);

        Space gap = new Space(this);
        content.addView(gap, new LinearLayout.LayoutParams(1, 0, 1));

        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(18, 18, 18, 18);
        panel.setBackground(rounded(0xE91A1222, 0xFFFF426D));
        content.addView(panel, new LinearLayout.LayoutParams(-1, -2));

        panel.addView(text("ANA MENÜ", 13, 0xFFFFA9BD, true));
        TextView headline = text("Minecraft'a hoş geldin", 22, Color.WHITE, true);
        headline.setPadding(0, 8, 0, 14);
        panel.addView(headline);

        Button launch = button("▶   Minecraft Bedrock'ı aç");
        panel.addView(launch, lp());
        launch.setOnClickListener(v -> launchBedrock());

        Button settings = button("⚙   AMZ arayüz ayarları");
        panel.addView(settings, lp());
        settings.setOnClickListener(v -> showSettings());

        Button about = button("ⓘ   Hakkında");
        panel.addView(about, lp());
        about.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("AMZ CLIENT")
            .setMessage("Bu APK, Bedrock için yardımcı başlatıcı/arayüz prototipidir. Minecraft'ın kendi oyun kodunu değiştirmez.")
            .setPositiveButton("Tamam", null).show());

        status = text("Hazır • Minecraft Bedrock 1.21.93 hedefleniyor", 12, Color.WHITE, false);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 16, 0, 0);
        content.addView(status);

        Space bottom = new Space(this);
        content.addView(bottom, new LinearLayout.LayoutParams(1, 0, 1));
    }

    private LinearLayout.LayoutParams lp() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 54);
        p.topMargin = 9; return p;
    }

    private void launchBedrock() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.mojang.minecraftpe");
        if (i != null) startActivity(i);
        else new AlertDialog.Builder(this)
            .setTitle("Minecraft bulunamadı")
            .setMessage("Önce Minecraft Bedrock'ı bu cihaza yükle. Bu uygulama Minecraft'ın yerine geçmez.")
            .setPositiveButton("Tamam", null).show();
    }

    private void showSettings() {
        LinearLayout box = new LinearLayout(this);
        box.setPadding(20, 8, 20, 8);
        box.setOrientation(LinearLayout.VERTICAL);
        addToggle(box, "FPS göstergesi (arayüz taslağı)");
        addToggle(box, "CPS göstergesi (arayüz taslağı)");
        addToggle(box, "Yerel rainbow halka görünümü (demo)");
        addToggle(box, "Menü düğmelerini kişiselleştir");
        new AlertDialog.Builder(this).setTitle("AMZ kişiselleştirme")
            .setView(box).setPositiveButton("Kapat", null).show();
    }

    private void addToggle(LinearLayout parent, String name) {
        Switch s = new Switch(this);
        s.setText(name); s.setTextColor(Color.WHITE);
        parent.addView(s);
    }

    @Override protected void onResume() {
        super.onResume();
        if (video != null && !video.isPlaying()) video.start();
    }
    @Override protected void onPause() {
        if (video != null && video.isPlaying()) video.pause();
        super.onPause();
    }
}
