# Atoms Cloud Proje Bağlamı (ATOMS.md)

## Servis Durumu ve Entegrasyon
- **Auth:** Atoms Cloud dahili kimlik doğrulama sistemi aktiftir. Her yetiştirici yalnızca kendi sürüsünü ve analiz kayıtlarını görür.
- **Database:** PostgreSQL `animals` (sürü kayıtları) ve `analyses` (fotoğraf ve sensör analizleri) tabloları.
- **File Storage:** Object Storage `oviscan-photos` kovası — saha fotoğraflarının yüklenmesi ve saklanması.
- **Edge Functions:** `/api/v1/oviscan/analyze-photo` ve `/api/v1/oviscan/seed` uçları.
- **AI Capabilities:** `gemini-3.1-pro-preview` multimodal görsel modeli (BCS, topallık ve sürü sağlığı analizi).

## Mimari ve Veri Akışı
1. Kullanıcı PWA üzerinden kamerayla veya galeriden koyun fotoğrafı yükler.
2. Fotoğraf Object Storage'a iletilir ve Edge Function üzerinden `gemini-3.1-pro-preview` modeline gönderilir.
3. Modelden dönen yapılandırılmış JSON (BCS, güven, bulgular, adımlar) `analyses` tablosuna yazılır.
4. İstemci PWA, internet kesilse dahi Service Worker ve yerel veritabanı (IndexedDB/LocalStorage) ile kesintisiz çalışmayı sürdürür.
