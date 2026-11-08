# AMZ Native — ilk yerel mod iskeleti

Bu proje, LeviLaunchroid / LeviLauncher Android Preloader SDK'si için ARM64 (`arm64-v8a`) native mod başlangıcıdır.

## Hedef

- Minecraft Bedrock 1.21.93 için paket manifesti.
- Android NDK ile `libamz_native.so` derlemek.
- Modu LeviLauncher'ın tanıdığı `.levipack` paketine koymak.
- İlk aşamada yüklenme / açılma / kapanma yaşam döngüsünü loglamak.

## Derleme

GitHub Actions içinden **Build AMZ Native Module** iş akışını çalıştır. Başarılı derleme sonunda `AMZ-NATIVE-arm64` artifact'inde `.so` ve `.levipack` bulunur.

## Önemli sınır

Bu ilk sürüm yalnızca native modun yüklenmesini doğrulayan bir iskelettir. Henüz KillAura, Scaffold, ESP veya AMZ dış yuvarlak menüsü bağlantısı içermez. Bunları çalışıyor gibi göstermiyoruz. Önce gerçek mod yüklemesini doğrulayacağız; sonra desteklenen SDK arayüzleriyle özellikleri ve AMZ kontrol bağlantısını ayrı ayrı geliştireceğiz.

Yalnızca kendi Minecraft kurulumunda ve izin verilen dünyalarda/sunucularda test et. Çok oyunculu sunucuların kuralları ve sunucu tarafı yetkileri ayrıca geçerlidir.