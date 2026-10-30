@echo off
chcp 65001 > nul
echo ========================================================
echo  OVISCAN - GITHUB DEPOLAMA VE DEMO YAYINLAMA BETIGI
echo ========================================================
echo.

set "GIT_CMD=git"
where git >nul 2>nul
if %errorlevel% neq 0 (
    if exist "%USERPROFILE%\.gemini\antigravity\bin\git\cmd\git.exe" (
        set "GIT_CMD=%USERPROFILE%\.gemini\antigravity\bin\git\cmd\git.exe"
        echo [OK] Yerleşik taşınabilir Git bulundu: %GIT_CMD%
    ) else (
        echo [UYARI] Git bulunamadı.
        pause
        exit /b
    )
)

echo.
echo [1/4] Git deposu güncelleniyor...
"%GIT_CMD%" init
"%GIT_CMD%" config user.name "Murat Erman"
"%GIT_CMD%" config user.email "murat.erman@example.com"

echo [2/4] Dosyalar ekleniyor...
"%GIT_CMD%" add .

echo [3/4] Commit yapılıyor...
"%GIT_CMD%" commit -m "feat: Initial commit - OviScan sheep pregnancy screening app and live web demo" 2>nul
"%GIT_CMD%" branch -M main

echo [4/4] GitHub'a gönderiliyor...
"%GIT_CMD%" remote remove origin 2>nul
"%GIT_CMD%" remote add origin https://github.com/mserman90/oviscan-android.git
"%GIT_CMD%" push -u origin main

echo.
echo ========================================================
echo  ISLEM TAMAMLANDI!
echo ========================================================
echo  Demo adresi (Pages açıldığında):
echo  https://mserman90.github.io/oviscan-android/
echo ========================================================
pause
