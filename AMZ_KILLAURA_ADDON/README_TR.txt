AMZ CLIENT - KILLAURA PROTOTYPE

Bu davranis paketi, etkin oldugu Minecraft Bedrock dunyalarinda etkinlestirilen oyuncuyu
haric tutarak yakinindaki diger oyunculari ve moblari hedefleyebilir.

KURULUM
1. GitHub Actions icinde "Build AMZ KillAura Add-on" islemini calistir.
2. Olusan AMZ-KILLAURA-MCPACK artifact dosyasini indir.
3. .mcpack dosyasini Android'de Minecraft ile acarak ice aktar.
4. Kendi dunyanda Duzenle > Davranis Paketleri bolumunden AMZ paketini etkinlestir.
5. Dunyanin PvP/komut ayarlarinin hedef oyuncuya hasara izin verdiginden emin ol.
6. Dunyaya girince acmak icin: /tag @s add amz_killaura
7. Kapatmak icin: /tag @s remove amz_killaura

AYARLAR
scripts/main.js dosyasinda:
- RANGE: hedef algilama mesafesi (blok)
- DAMAGE: her vurusun hasari
- HIT_INTERVAL_TICKS: ayni hedefe vurus araligi (20 tick = 1 saniye)

SINIRLAMALAR
- Paket, sadece davranis paketinin yuklu ve etkin oldugu dunyalarda calisir.
- Cok oyunculu sunucuda sunucunun bu paketi yuklemesi/izin vermesi gerekir.
- Herkese acik sunucularda veya PvP kapali dunyalarda oyunculara hasar garanti degildir.
- AMZ'nin kayan menu anahtari henuz bu oyun-ici scripte bagli degildir.
- Bu prototipin Android/Minecraft 1.21.93 uzerinde gercek cihaz testi henuz yapilmamistir.
