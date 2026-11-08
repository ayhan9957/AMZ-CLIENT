AMZ CLIENT - KILLAURA MOB TEST

Bu ilk gerçek oyun-içi prototiptir. Yalnızca davranış paketinin etkin olduğu
Minecraft Bedrock dünyalarında çalışır. Herkese açık sunuculara kendiliğinden
bağlanmaz ve oyuncuları hedef almaz; ilk test yalnızca moblara hasar verir.

KURULUM
1. Bu klasörü bir .mcpack dosyasına dönüştürüp Android'e aktar.
2. Minecraft ile açarak davranış paketini içe aktar.
3. Kendi dünyanda Düzenle > Davranış Paketleri bölümünden AMZ paketini etkinleştir.
4. Dünya ayarlarında gerekli komut/deney izinlerini aç.
5. Dünyaya girince KillAura'yı açmak için:
   /tag @s add amz_killaura
6. Kapatmak için:
   /tag @s remove amz_killaura

AYARLAR
scripts/main.js dosyasında:
- RANGE: hedef algılama mesafesi (blok)
- DAMAGE: her vuruşta hasar
- HIT_INTERVAL_TICKS: vuruş aralığı (20 tick = 1 saniye)

ÖNEMLİ
Bu paket şu an AMZ'nin kayan menüsüne bağlı değildir. Menü anahtarı henüz
Minecraft içindeki script'e sinyal gönderemiyor. Bu bağlantı ayrı bir sonraki
geliştirme adımıdır. Çok oyunculu sunucuda çalışması, sunucunun bu paketi
yüklemesine ve izin vermesine bağlıdır.
