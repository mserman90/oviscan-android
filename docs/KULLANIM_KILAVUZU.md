# OviScan Çiftçi ve Yetiştirici Kullanım Kılavuzu 🐑📱
### Akıllı Telefon ile Koyunlarda Gebelik Ön Tarama, BCS Kondisyon ve Sürü Takip Rehberi

* **Canlı PWA Uygulama Adresi:** [https://mserman90.github.io/oviscan-android/](https://mserman90.github.io/oviscan-android/)
* **Açık Kaynak Kod Deposu:** [https://github.com/mserman90/oviscan-android](https://github.com/mserman90/oviscan-android)
* **Paket İndirme:** [`oviscan-android-release.zip`](file:///C:/Users/murat.erman/Downloads/oviscan-android-release.zip)

---

> [!CAUTION]
> ### ⚠️ EN BAŞTA BİLİNMESİ GEREKEN ÇOK ÖNEMLİ KURAL:
> Bu uygulama **bir veteriner hekim ultrason cihazı veya kan tahlili DEĞİLDİR**.  
> Cebinizdeki telefonun dahili mikrofonu, ivmeölçeri ve kamerasıyla çalışan bir **"Ön Eleme ve Karar Destek Aracıdır"**.  
> **Asla sadece bu uygulamaya güvenerek bir koyunu "kesin boş" diye kasaba veya kesime göndermeyiniz; "kesin gebe" diyerek resmi veteriner muayenesinden vazgeçmeyiniz!**

---

## 1. Kullanıcı Girişi, Demo Hesabı ve Yeni Kayıt

OviScan, sürünüzün kayıtlarını güvenle saklar. İnternetiniz olmasa bile telefonunuzun kendi hafızasında tüm veriler korunur.

```
┌────────────────────────────────────────────────────────────────────────┐
│                   GİRİŞ SEÇENEKLERİ HANGİLERİDİR?                      │
├────────────────────────────────────────────────────────────────────────┤
│ 1. Demo Hesabı ile Hızlı Giriş: Şifre yazmadan 142 koyunluk hazır      │
│    Gökçeçayır sürüsüyle sistemi hemen deneyebilirsiniz.                │
│ 2. Yeni Çiftçi Kaydı: Kendi çiftliğinizin adını girerek sıfırdan       │
│    kendi sürünüzü oluşturabilirsiniz.                                  │
│ 3. Şifremi Unuttum: Telefonunuza gelen 4 haneli kodla şifrenizi        │
│    10 saniyede yenileyebilirsiniz.                                     │
└────────────────────────────────────────────────────────────────────────┘
```

### A. Demo Hesabı ile Tek Tıkla Başlama (En Hızlı Yol)
1. Uygulamayı açtığınızda ekranda beliren **"🚀 Demo Hesabı ile Tek Tıkla Giriş Yap"** butonuna basınız.
2. Hiçbir şifre girmeden, 142 başlık hazır Merinos, Akkaraman ve İvesi sürüsü (Gökçeçayır İşletmesi) yüklü olarak açılacaktır.
3. Bu hesapta hazır gebe adaylarını, dikkat gerektiren hayvanları ve geçmiş analizleri hemen inceleyebilirsiniz.

### B. Kendi Çiftliğiniz İçin Yeni Kayıt Açma
1. Giriş ekranında **"📝 Yeni Çiftçi Kaydı"** sekmesine dokununuz.
2. Adınızı Soyadınızı, Çiftlik / İşletme Adınızı (Örn: *Bozkır Damızlık Çiftliği*), Şehir / İlçenizi ve E-posta veya Telefonunuzu yazınız.
3. Kendinize en az 3 karakterli kolay hatırlayabileceğiniz bir şifre belirleyiniz.
4. **"Kayıt Ol ve İşletmeni Başlat"** butonuna basınız. Kendi özel çiftlik paneliniz anında hazır olacaktır!

### C. Şifremi Unuttum (Şifre Yenileme)
1. Giriş ekranında **"Şifremi Unuttum?"** yazısına dokununuz.
2. Kayıtlı e-posta veya telefon numaranızı yazıp **"Doğrulama Kodu Gönder"**e basınız.
3. Ekranda beliren 4 haneli onay kodunu (Örn: `8492`) ilgili kutuya giriniz.
4. Yeni şifrenizi yazıp **"Şifremi Yenile ve Giriş Yap"**a basınız. Şifreniz anında güncellenir.

---

## 2. Canlı Gebelik Ön Taraması Nasıl Yapılır? (Çekirdek Özellik)

OviScan, **harici hiçbir ultrason probu, kablo veya cihaz gerektirmeden**, yalnızca telefonun dahili mikrofonu ve ivmeölçeriyle çalışır.

### A. Ölçüm İçin En Doğru Zaman Ne Zamandır?
* **Koç Katımı / Tohumlama Tarihi Şarttır:** Koyunun koça verildiği günü mutlaka sisteme girmelisiniz.
* **17 Gün Kuralı (Kızgınlık Döngüsü):** Gebe kalmayan koyun koç katımından yaklaşık **16–17 gün sonra** tekrar koç arar, huzursuzlaşır ve sık melemeye başlar.
* **Neden 25. Günden Sonra?** 17. günü sakin geçiren, geviş ritmi oturan ve dinlenme süresi artan koyunda gebelik ihtimali matematiksel olarak güçlenir.
* **En İdeal Ölçüm Aralığı:** Tohumlamadan sonraki **25. gün ile 45. gün arasıdır**.

---

### B. Ölçüm Öncesi Hazırlık — Ortam ve Hayvan Koşulları

> [!IMPORTANT]
> Telefon doğru tutulsa bile ortam gürültüsü veya hayvanın huzursuzluğu sonucu bozabilir. Aşağıdaki koşulları sağlamadan ölçüme başlamayın.

```
ÖLÇÜM ÖNCESİ KONTROL LİSTESİ
═══════════════════════════════════════════════════════════════
✅  Koyun tohumlanmadan / koç katımından en az 25 gün geçti mi?
✅  Traktör, yem karma makinesi, köpek — uzakta / kapalı mı?
✅  Koyun sakin bir bölmede ya da yemlik kenarında mı duruyor?
✅  Rüzgarlı hava varsa arkam rüzgara dönük mü — telefona siper mi oluyorum?
✅  Koyunu koşturmadım, en az 5 dakika dinlendirdim mi?
✅  Telefon sesi kısık, bildirimler "Rahatsız Etme" modunda mı?
═══════════════════════════════════════════════════════════════
```

---

### C. 📱 Telefonu Doğru Tutma Pozisyonu (En Kritik Bölüm)

> [!CAUTION]
> **Telefonun yanlış tutulması veya sallanması**, ivmeölçerin yanlış veri üretmesine yol açar ve sonuç bozulur. Bu bölümü dikkatlice okuyun.

**Telefonu Hangi Elinizle ve Nasıl Tutacaksınız?**

```
DOĞRU TUTUŞ DİYAGRAMI (Önden Bakış — Siz Koyunun Sol Yanında Duruyorsunuz)
════════════════════════════════════════════════════════════════════════════

         [SİZ]
           |
   Sol elin avucu      ←── Telefonu YATEYİ tutun
   ekranı size baksın  ←── Telefonun ALT KENARI koyuna dönük
           |
           ↓  20 – 30 cm  ↓
   ┌───────────────────┐
   │  KOYUNUN          │
   │  ← SOL BÖĞRÜ →   │   ← İşkembe / Sol Açlık Çukuru
   │   (işkembe)       │
   └───────────────────┘

NOT: Koyunun sağ böğrü değil, MUTLAKA SOL BÖĞRÜ (sol açlık çukuru) kullanılır.
     İşkembe (büyük mide) sol taraftadır; geviş sesleri buradan yükselir.
```

**Adım Adım Tutuş Talimatı:**

1. **Telefonun ALT kenarını** (mikrofon delikleri olan kısım) koyunun yönüne çevirin.  
   → Mikrofon delikleri genellikle telefonun en altındaki şarj girişinin yanındadır.

2. **Sol elinizle telefonu YATAY** tutun; ekran size bakacak şekilde konumlandırın.  
   → Dikey (portrait) tutmayın — yatay (landscape) tutmanıza gerek yok; önemli olan **ALT MİKROFONUN koyuna bakması**.

3. **Koyunun SOL böğrü (açlık çukuru / işkembe bölgesi)** hizasında durun.  
   → Koyunun sağ tarafına geçmeyin; işkembe sol taraftadır, geviş ve solunum sesleri oradan gelir.

4. **20 – 30 cm mesafeyi koruyun.**  
   → Çok yakın (5–10 cm) tutmak koyunun nefes sesini değil kumaş/yün sürtme gürültüsünü alır.  
   → Çok uzak (50+ cm) tutmak sesi zayıflatır.

5. **Kameranın da koyunun karın / böğür bölgesine bakmasını sağlayın.**  
   → Optik solunum sayımı (kamera ile) ve akustik analiz (mikrofon ile) eş zamanlı çalışır.

```
MESAFE REHBERİ
══════════════════════════════════════════════════════
  5 – 10 cm   ❌ Çok yakın  → Yün/kumaş gürültüsü baskın
 20 – 30 cm   ✅ İDEAL      → Geviş ve solunum net duyulur
 50+ cm       ❌ Çok uzak   → Sinyal zayıf, sonuç güvenilmez
══════════════════════════════════════════════════════
```

---

### D. Adım Adım Ölçüm Süreci (Numarasıyla Takip Edin)

```
ADIM 1 ──► Uygulamada "⚡ Gebelik" sekmesine girin
ADIM 2 ──► Açılır listeden koyunun küpe numarasını seçin
ADIM 3 ──► Mod seçin:
           • "Elde Hızlı Tarama (30 sn)" — ahırda bizzat tutacaksanız
           • "Tasma / Koşum Kipi (60 sn)"— telefonu bez ceple koyunun
             boynuna / göğsüne bağlayacaksanız
ADIM 4 ──► "▶ Ölçümü Başlat" düğmesine basın
ADIM 5 ──► Ekranda yeşil dalgalar akarken telefonu SABİT TUTUN
           (sallamamak çok önemli — ivmeölçer hareketi kaydeder!)
ADIM 6 ──► 30 saniye sonra telefon hafifçe titrer (vibrasyon)
ADIM 7 ──► Trafik ışığı rengini ve % ihtimalini okuyun
ADIM 8 ──► "💾 Sonucu Kaydet" butonuna basarak hayvan dosyasına yazın
```

---

### E. Tasma / Koşum Kipi — Eller Serbest Kullanım

Koyunları tek tek tutarken yardımcınız yoksa veya büyük bir sürüyü hızlıca taramak istiyorsanız **Tasma Kipini** kullanabilirsiniz:

1. Telefonunuzu ince bir bez cebe veya nefes alabilir küçük bir kese içine koyun.
2. Cebi koyunun **göğüs bölgesine** ya da **boynuna** bağlayın (tasma veya kayışla).
3. Uygulamada **"Tasma / Koşum Kipi (60 sn)"** seçeneğini seçin.
4. Koyunu sakin bir bölmede bırakın; 60 saniye boyunca dinlenmesine izin verin.
5. Süre bitince telefonu alıp sonucu okuyun.

> **İpucu:** Tasma kipinde mikrofon tam böğre değil, göğüs bölgesine yakın olur. Sonuçlar biraz daha genel olabilir; şüpheli çıkan hayvanları "Elde Hızlı Tarama" ile bir kez daha ölçün.

---

### F. Sık Yapılan Hatalar ve Nasıl Önlenir?

```
HATA                    │ SONUCA ETKİSİ            │ ÇÖZÜM
────────────────────────┼──────────────────────────┼──────────────────────────
Telefonu sallamak       │ İvmeölçer yanlış veri    │ Bileği sabitleyin,
                        │ üretir → Sarı/Kırmızı    │ dirsekle destekleyin
────────────────────────┼──────────────────────────┼──────────────────────────
Traktör/köpek gürültüsü │ Mikrofon arka plan       │ Motoru kapatın veya
                        │ sesle dolar → Düşük skor │ sakin bölmeye geçin
────────────────────────┼──────────────────────────┼──────────────────────────
Sağ böğür yerine sağ    │ Geviş sesi zayıf gelir   │ Koyunun sol tarafına
tarafta ölçüm           │                          │ geçin
────────────────────────┼──────────────────────────┼──────────────────────────
Koyunu koşturarak       │ Huzursuzluk koç arama    │ 5 dk dinlendirin,
hemen ölçmek            │ davranışıyla karışır     │ sonra ölçün
────────────────────────┼──────────────────────────┼──────────────────────────
5 cm'den yakın tutmak   │ Yün/kumaş gürültüsü      │ 20-30 cm mesafeyi
                        │ mikrofonla yarışır        │ koruyun
────────────────────────┼──────────────────────────┼──────────────────────────
Rüzgarda dışarıda       │ Rüzgar mikrofona çarpar  │ Vücudunuzu siper yapın
ölçmek                  │ → Yanlış sonuç           │ ya da ahıra girin
```

---

### G. Sonuçları Okuma (Trafik Işığı Sistemi)
* 🟢 **YEŞİL KART (Gebe Olma Olasılığı Yüksek > %70):**  
  Hayvan 17 günlük kızgınlık evresini sakin aşmış; geviş akustikleri düzenli ve meleme atağı yoktur.  
  *Çiftçi Ne Yapmalı?* Hayvanı gebe besleme rasyonuna alınız. Yavru sayısını (tekiz/ikiz) tescil etmek için veteriner hekiminize ultrason randevusu alınız.
* 🟡 **SARI KART (Belirsiz / Tekrar Tarama Gerekli %30 - %70):**  
  Telefonun aldığı sinyaller sınır değerdedir (ölçüm anında gürültü olmuş veya hayvan kımıldamış olabilir).  
  *Çiftçi Ne Yapmalı?* **Hayvanı asla boş saymayınız!** 7-10 gün sonra sessiz bir ortamda ölçümü bir kez daha tekrarlayınız.
* 🔴 **KIRMIZI KART (Gebe Olma İhtimali Düşük / Boş Adayı < %30):**  
  Koyunda huzursuz hareketlilik (koç arama davranışı), sık tiz meleme saptanmıştır ve tohumlama günü kızgınlık nüksüne denk gelmektedir.  
  *Çiftçi Ne Yapmalı?* İkinci tohumlama veya koç katımı takibine alınız. Kesinlikle veteriner muayenesi olmadan kasaba göndermeyiniz!

---

## 3. Fotoğraftan AI Vücut Kondisyon Skoru (BCS) & Topallık Analizi

Bu modül, telefon kamerasıyla çektiğiniz tek bir fotoğraftan koyunun besleme durumunu ve ayak sağlığını yapay zeka ile inceler.

### A. Fotoğrafı Nasıl Çekmelisiniz?
1. Koyunun **yan ve hafif çapraz arka açısından** (omurga hattı, lumbosakral bel bölgesi ve böğür çukurluğu net görünecek şekilde) durunuz.
2. Yaklaşık 1.5 - 2 metre mesafeden, gün ışığında veya iyi aydınlatılmış ahırda net bir fotoğraf çekiniz.
3. İster canlı kameradan çekebilir, ister albümden yükleyebilir, isterseniz sistemdeki **3 Örnek Fotoğrafı (İdeal, Zayıf, Topallık)** seçerek hemen deneyebilirsiniz.

### B. BCS Skoru Ne Anlama Gelir?
* **1.0 - 2.0 (Aşırı Zayıf / Kaşektik):** Omurga kemikleri belirgin ve keskindir. Enerji açığı vardır; acil ezme arpa ve melas takviyesi gerekir.
* **3.0 - 3.5 (İDEAL KONDİSYON):** Damızlık ve gebelik için en verimli aralıktır. Omurga üzeri düzgün yumuşak yağ örtüsüyle kaplıdır.
* **4.0 - 5.0 (Aşırı Yağlı / Obez):** Doğum güçlüğü ve ketozis riski taşır; rasyondaki kaba yem artırılmalıdır.

### C. Topallık ve Ağrı Tespiti
Yapay zeka bacak basış açısını ve sırt kamburlaşmasını inceler. Ayakta yük sakınımı varsa tırnak çürüğü (piyetin) kontrolü önerir.

---

## 4. Sürü Takibi ve Hayvan Dosyaları

* **Hayvan Listesi:** Sürünüzdeki tüm koyunları küpe numarası, ırkı, yaşı ve güncel gebelik/sağlık durumuyla listeler.
* **Detay Dosyası:** Bir hayvana tıkladığınızda tohumlamadan sonra kaç gün geçtiğini, geçmiş gebelik taramalarını ve fotoğraflı BCS analizlerini tek ekranda görürsünüz.
* **Yeni Hayvan Kaydı:** Sağ üstteki **"➕ Yeni Hayvan Ekle"** butonuna basarak küpe no, ırk, cinsiyet ve tohumlama tarihini girip saniyeler içinde kaydedebilirsiniz.

---

## 5. Raporu Veteriner Hekime WhatsApp'tan Gönderme

Tüm sürünüzü taradıktan sonra:
1. Ekranın sağ üstündeki **"📥 CSV İndir"** butonuna basınız.
2. Tüm sürünün küpe numaraları, gebelik olasılıkları ve BCS skorları tek bir Excel/CSV dosyası olarak iner.
3. Telefonunuzun paylaşım menüsünden **WhatsApp**'ı seçip veteriner hekiminize tek tıkla gönderiniz.
4. Hekim ultrasonla muayene yaptığında, sonuç ekranındaki **"✓ Gebe Doğrulandı"** veya **"✗ Boş Doğrulandı"** butonlarına basarak resmi sonucu tescil ediniz.

---

## 6. Sıkça Sorulan Sorular (SSS)

**Soru 1: Bu telefon kuzu kalp atışını duyar mı?**  
> *Cevap:* **Hayır.** Kuzu kalbi koyunun kalın derisi altından sadece özel veteriner ultrason problarıyla duyulur. Telefon mikrofonu annenin **solunum ritmini, işkembe geviş seslerini ve kızgınlık melemesi olup olmadığını** dinler.

**Soru 2: Çiftlikte veya dağda internet çekmiyor, çalışır mı?**  
> *Cevap:* **Evet.** OviScan bir PWA'dır. Telefonunuza bir kez yüklendikten sonra, internet tamamen kapalıyken bile hem gebelik taraması hem sürü kayıtları çevrimdışı çalışır.

**Soru 3: Herhangi bir prob, kablo veya cihaz satın almam gerekiyor mu?**  
> *Cevap:* **Kesinlikle hayır.** Yalnızca elinizdeki standart Android veya iPhone telefon yeterlidir. Sıfır ek donanım masrafıyla çalışır.

**Soru 4: Sonuç "Kırmızı" çıktı, koyunu kasaba satabilir miyim?**  
> *Cevap:* **KESİNLİKLE HAYIR!** Bu bir ön eleme aracıdır. Gebe bir koyunun kesilmesi büyük maddi ve vicdani kayıptır. Şüpheli hayvanları mutlaka veteriner hekiminize ultrasonla teyit ettiriniz.
