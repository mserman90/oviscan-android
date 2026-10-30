# OviScan Backend (Atoms Cloud Edge Functions & PostgreSQL)

Bu modül, OviScan PWA uygulamasının bulut altyapısını, veri şemalarını ve multimodal AI uçlarını barındırır.

## Hazır Servisler
1. **Auth:** Kullanıcı kaydı, oturum yönetimi ve kullanıcı bazlı RLS (Row Level Security).
2. **Database:** PostgreSQL tabanlı `animals` ve `analyses` tabloları.
3. **File Storage:** Object Storage bucket `oviscan-photos` — saha fotoğraflarının yüklenmesi ve önizlenmesi.
4. **Edge Functions:**
   - `POST /api/v1/oviscan/analyze-photo`: Çoklu görsel destekli `gemini-3.1-pro-preview` ile BCS, topallık ve sağlık analizi.
   - `POST /api/v1/oviscan/seed`: Yeni işletme için örnek sürü verisi yükleme.
