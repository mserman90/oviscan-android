# OviScan Sistem Mimarisi & Karar Destek Çerçevesi

## 1. Genel Mimari Bakışı
OviScan, koyun yetiştiricilerinin sahada (ahır, mera, açık alan) hem mobil telefonlardan hem de masaüstü terminallerden harici pahalı ultrason probları veya laboratuvar test kitleri olmaksızın kullanabilecekleri, multimodal bir **Sürü Sağlığı ve Gebelik Karar Destek PWA Platformu**dur.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            OviScan PWA İstemcisi                            │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ Editoryal Tasarım Sistemi (Sıcak-Nötr, Sıfır Gölge, Hairline Sınırlar)  │ │
│ │ GSAP Animasyon Motoru (power2.out, prefers-reduced-motion desteği)      │ │
│ └─────────────────────────────────────────────────────────────────────────┘ │
│ ┌───────────────┬─────────────────┬────────────────────┬────────────────────┬──────────────────┐ │
│ │  Sürü Panosu  │ Canlı Gebelik   │ Fotoğraftan AI     │ Hayvan Kayıtları   │ PWA Kabuğu       │ │
│ │  (Flock KPI & │ Taraması        │ (BCS, Topallık &   │ (Küpe Listesi &    │ (Çevrimdışı SW & │ │
│ │  Öncelikler)  │ (Mic + IMU + 17)│ Karar Destek)      │ Detay Dosyası)     │ Manifest)        │ │
│ └───────────────┴─────────────────┴────────────────────┴────────────────────┴──────────────────┘ │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                ┌──────────────────────┴──────────────────────┐
                ▼                                             ▼
┌────────────────────────────────┐         ┌────────────────────────────────┐
│   İstemci İçi Çevrimdışı Katman│         │  Atoms Cloud Backend Katmanı   │
│  - Service Worker (sw.js)      │         │  - Auth (Kullanıcı Oturumu)    │
│  - IndexedDB & LocalStorage    │◄───────►│  - PostgreSQL (animals,        │
│  - Canlı Kamera & Görsel Motor │         │               analyses)        │
│  - 100% Çevrimdışı Çalışma     │         │  - Object Storage (Fotoğraflar)│
└────────────────────────────────┘         │  - Edge Functions (AI API)     │
                                           │  - Gemini 3.1 Pro Vision Model │
                                           └────────────────────────────────┘
```

## 2. Tasarım Sistemi ve Görsel Dil
- **Renk Paleti:** Sıcak-nötr kırık beyaz zemin (`#F9F8F6`), yumuşak siyah metin (`#1A1918`), doğal zeytin yeşili (`#2C5E3B`), koyu toprak ochre (`#8F641B`) ve terrakotta (`#94382E`).
- **Tipografi:** Başlıklarda normal ağırlıkta editoryal serif (`Newsreader`, `Lora`, Georgia), arayüz gövdesinde nötr sans (`Inter`, system-ui).
- **Derinlik ve Yapı:** Sıfır kutu gölgesi (`box-shadow: none`); derinlik yalnızca `1px solid #E5E2DC` saç teli (hairline) çizgiler ve açık alanlarla sağlanır.
- **Form Öğeleri:** Paneller ve giriş alanları köşesiz; buton, rozet ve etiketler tam yuvarlak hap (pill - `border-radius: 9999px`).
- **Mikro Etiketler (Eyebrow):** Geniş harf aralıklı büyük harf etiketler (`letter-spacing: 0.14em; text-transform: uppercase`).
- **Erişilebilirlik:** WCAG 2.2 AA uyumu, tüm etkileşimli öğelerde `focus-visible` halkası, ≥44px dokunma hedefleri.

## 3. Multimodal AI Analiz Boru Hattı
1. **Görsel Giriş:** Kameradan canlı çekim veya galeriden yükleme (mobil & masaüstü).
2. **Ön İşleme & Çıkarım:**
   - Omurga (lumbosakral) ve kaburga yağ örtüsü konturu
   - Böğür derinliği ve temassız solunum frekansı (BPM)
   - Bacak açısı ve simetrik yük basış dengesi (topallık/ağrı)
3. **AI Çıktı Modeli:**
   - `bcs_score`: 1.0 - 5.0 (0.25 hassasiyet)
   - `posture_gait`: Topallık ve duruş durumu
   - `findings`: 3-4 maddelik somut anatomik gözlem
   - `recommendations`: Yetiştirici için besleme ve veteriner aksiyonları
4. **Kayıt ve Takip:** Sonuç tek dokunuşla hayvanın geçmişine ve veritabanına işlenir.

## 4. İkincil Modüller ve Yer Tutucular
- **Sesli Not Analizi:** Saha ses kaydı alıp dikte etme (Whisper / Gemini STT yer tutucu).
- **Karar Destek Asistanı:** Sürüye özel RAG sohbet asistanı yer tutucusu.
- **Sağlık Takvimi:** Aşı, ilaç ve parazit takip modülü yer tutucusu.
- **Üreme Takibi:** 17 günlük östrus siklusu ve ultrason doğrulaması yer tutucusu.
