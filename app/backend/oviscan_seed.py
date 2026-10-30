"""
OviScan Seed Data Generator for New Farmers
Populates the flock with 14 realistic sheep records across Akkaraman, Merinos, İvesi, Sakız breeds.
"""

from datetime import datetime, timedelta
import random

SAMPLE_ANIMALS = [
    {"tag_id": "TR-06-84920", "breed": "Akkaraman", "gender": "Dişi", "age_months": 28, "status": "Düşük Kondisyon (BCS)", "pen_number": "Ağıl-2", "days_ago": 32, "bcs": 1.75},
    {"tag_id": "TR-06-84915", "breed": "Merinos", "gender": "Dişi", "age_months": 36, "status": "Boş / Östrus Şüphesi", "pen_number": "Ağıl-1", "days_ago": 17, "bcs": 3.25},
    {"tag_id": "TR-06-84882", "breed": "İvesi", "gender": "Dişi", "age_months": 24, "status": "Tedavide (Topallık)", "pen_number": "Karantina-1", "days_ago": 45, "bcs": 2.50},
    {"tag_id": "TR-06-84941", "breed": "Akkaraman", "gender": "Dişi", "age_months": 30, "status": "Şüpheli (Tekrar Ölçüm)", "pen_number": "Ağıl-3", "days_ago": 28, "bcs": 3.00},
    {"tag_id": "TR-06-84901", "breed": "Merinos", "gender": "Dişi", "age_months": 42, "status": "Gebe Adayı (Yüksek)", "pen_number": "Ağıl-1", "days_ago": 38, "bcs": 3.50},
    {"tag_id": "TR-06-84903", "breed": "Akkaraman", "gender": "Dişi", "age_months": 22, "status": "Gebe Adayı (Yüksek)", "pen_number": "Ağıl-2", "days_ago": 41, "bcs": 3.20},
    {"tag_id": "TR-06-84904", "breed": "Sakız", "gender": "Dişi", "age_months": 18, "status": "Gebe Adayı (Yüksek)", "pen_number": "Ağıl-4", "days_ago": 26, "bcs": 3.10},
    {"tag_id": "TR-06-84907", "breed": "İvesi", "gender": "Dişi", "age_months": 34, "status": "Gebe Adayı (Yüksek)", "pen_number": "Ağıl-3", "days_ago": 35, "bcs": 3.40},
    {"tag_id": "TR-06-84911", "breed": "Kıvırcık", "gender": "Dişi", "age_months": 26, "status": "Şüpheli (Tekrar Ölçüm)", "pen_number": "Ağıl-2", "days_ago": 19, "bcs": 2.80},
    {"tag_id": "TR-06-84918", "breed": "Akkaraman", "gender": "Dişi", "age_months": 40, "status": "Gebe Adayı (Yüksek)", "pen_number": "Ağıl-1", "days_ago": 49, "bcs": 3.30},
    {"tag_id": "TR-06-84925", "breed": "Merinos", "gender": "Dişi", "age_months": 20, "status": "Boş / Östrus", "pen_number": "Ağıl-1", "days_ago": 16, "bcs": 3.00},
    {"tag_id": "TR-06-84930", "breed": "Akkaraman", "gender": "Koç", "age_months": 48, "status": "Damızlık Koç", "pen_number": "Koç Bölmesi", "days_ago": None, "bcs": 3.75},
]

async def seed_user_flock(user_id: str, db_client):
    now = datetime.utcnow()
    records = []
    for item in SAMPLE_ANIMALS:
        insem_date = (now - timedelta(days=item["days_ago"])).isoformat() if item["days_ago"] else None
        records.append({
            "user_id": user_id,
            "tag_id": item["tag_id"],
            "breed": item["breed"],
            "gender": item["gender"],
            "age_months": item["age_months"],
            "status": item["status"],
            "pen_number": item["pen_number"],
            "insemination_date": insem_date,
            "latest_bcs": item["bcs"],
            "created_at": now.isoformat(),
            "updated_at": now.isoformat()
        })
    await db_client.table("animals").insert(records)
    return {"status": "success", "count": len(records)}
