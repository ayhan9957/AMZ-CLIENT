AMZ CLIENT — ANDROID APK PROJESİ

Bu klasör Android Studio kaynak projesidir; henüz derlenmiş APK değildir.
Kullanıcının gönderdiği MP4, ana menüde sessiz ve döngüsel arka plan videosu olarak
kullanılır. Uygulamadaki "Minecraft Bedrock'ı aç" düğmesi cihazdaki resmi
com.mojang.minecraftpe uygulamasını başlatır.

Mimari:
- Proje Java tabanlıdır ve kendi yerel native .so kütüphanesi içermez.
- Bu nedenle tek APK normalde Android ARM32 ve ARM64 cihazlarda çalışabilecek
  şekilde derlenebilir; cihazdaki Minecraft kurulumunun mimarisi ayrı konudur.

Derleme:
1. Bu ZIP'i çıkar.
2. Android Studio'da AMZ_CLIENT_APK_Project klasörünü aç.
3. Android SDK Platform 35 ve Android Gradle Plugin 8.7.3 indirilip eşitlensin.
4. Build > Build Bundle(s) / APK(s) > Build APK(s).
5. APK: app/build/outputs/apk/debug/app-debug.apk

Teknik sınır:
Bu, Bedrock'ı açan bağımsız bir yardımcı APK prototipidir; Minecraft'ın resmi
ana menüsünü/ESC ekranını değiştirmez, oyuna kod enjekte etmez ve hile/anti-ban
özelliği içermez. Bedrock'ın gerçek oyun içi ekranlarını değiştirmek için resmi
olarak desteklenen modlama/arayüz imkanları gerekir; MP4'ün Minecraft'ın kendi
ana menüsünde otomatik oynatılması sıradan bir Android APK ile garanti edilemez.
