"""
OviScan Multimodal AI Vision Analysis Edge Function
Model: gemini-3.1-pro-preview (Multimodal Vision + Veterinary Decision Support)
Endpoint: POST /api/v1/oviscan/analyze-photo
"""

import os
import json
import logging
from typing import Dict, Any, Optional

logger = logging.getLogger("oviscan.analysis")

VETERINARY_SYSTEM_PROMPT = """Sen deneyimli bir koyun ırkları uzmanı ve sürü sağlığı veteriner hekimisin.
Sana sunulan koyun fotoğrafını (omurga hattı, lumbosakral bölge, kaburgalar, böğür dolgunluğu, bacak duruşu ve yapağı durumu)
inceleyerek aşağıdaki ölçütlere göre yapılandırılmış bir klinik değerlendirme üret:

1. Vücut Kondisyon Skoru (BCS): 1.0 (Aşırı Zayıf/Kaşektik) ile 5.0 (Aşırı Yağlı/Obez) arasında 0.25 hassasiyetle skorla. İdeal aralık 3.0 - 3.5'tir.
2. Duruş ve Topallık (Gait/Posture): Ayak basış simetrisi, sırt kamburlaşması, ağrı işareti.
3. Temassız Solunum ve Genel Vital Belirti: Böğür hareket ritmi, göz/burun çevresi durumu.
4. Klinik Bulgular: 3-4 maddelik somut anatomik gözlem.
5. Saha Eylem Önerileri: Yetiştirici için derhal uygulanacak besleme, gebelik takvimi ve veteriner müdahale adımları.

Çıktıyı kesinlikle geçerli bir JSON objesi olarak şu formatta üret:
{
  "bcs_score": 3.25,
  "bcs_category": "İdeal Kondisyon",
  "confidence": 0.91,
  "risk_level": "Düşük",
  "posture_gait": "Normal basış, ağrı/topallık bulgusu yok",
  "findings": [
    "Bel omurları ve transvers çıkıntılar düzgün yağ tabakasıyla örtülü, elle muayenede hafif baskıyla hissedilebilir.",
    "Böğür çukurluğu dolgun, açlık çukuru normal derinlikte.",
    "Solunum ritmik ve sakin, istirahat frekansı gebelik dönemiyle uyumlu."
  ],
  "recommendations": [
    "Mevcut rasyonu koruyun, temiz suya serbest erişim sağlayın.",
    "Tohumlamanın 25. gününden sonra telefon mikrofonuyla solunum/geviş akustik gebelik taramasını başlatın.",
    "3 hafta sonra rutin BCS takibini tekrarlayın."
  ]
}
"""

async def handle_photo_analysis(request_data: Dict[str, Any], db_client, storage_client, ai_client) -> Dict[str, Any]:
    animal_id = request_data.get("animal_id")
    image_storage_path = request_data.get("image_path")
    user_id = request_data.get("user_id")

    if not animal_id or not image_storage_path:
        raise ValueError("animal_id ve image_path zorunludur.")

    # 1. Fotoğrafı Object Storage'dan oku
    image_bytes = await storage_client.download(image_storage_path)

    # 2. Multimodal AI çağrısı (Gemini Pro Vision)
    response = await ai_client.generate_content(
        model="gemini-3.1-pro-preview",
        system_instruction=VETERINARY_SYSTEM_PROMPT,
        contents=[
            {
                "role": "user",
                "parts": [
                    {"inline_data": {"mime_type": "image/jpeg", "data": image_bytes}},
                    {"text": "Bu koyunun vücut kondisyon skoru (BCS), topallık ve sağlık durumunu analiz et."}
                ]
            }
        ],
        generation_config={
            "response_mime_type": "application/json",
            "temperature": 0.2
        }
    )

    result_json = json.loads(response.text)

    # 3. Analizi Veritabanına Kaydet
    analysis_record = {
        "user_id": user_id,
        "animal_id": animal_id,
        "analysis_type": "photo_bcs",
        "image_url": image_storage_path,
        "bcs_score": result_json.get("bcs_score"),
        "confidence": result_json.get("confidence", 0.88),
        "risk_level": result_json.get("risk_level", "Düşük"),
        "posture_gait": result_json.get("posture_gait"),
        "findings": result_json.get("findings", []),
        "recommendations": result_json.get("recommendations", []),
        "ai_model": "gemini-3.1-pro-preview"
    }

    inserted_analysis = await db_client.table("analyses").insert(analysis_record)

    # 4. Hayvan kartındaki latest_bcs alanını güncelle
    await db_client.table("animals").update({
        "latest_bcs": result_json.get("bcs_score"),
        "updated_at": "now()"
    }).eq("id", animal_id)

    return {
        "status": "success",
        "analysis_id": inserted_analysis.get("id"),
        "data": result_json
    }
