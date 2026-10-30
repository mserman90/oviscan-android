# PowerShell GitHub Push & Release Automation for OviScan
Write-Host "========================================================" -ForegroundColor Green
Write-Host " OviScan - GitHub Depolama ve Pages Demo Yayınlama" -ForegroundColor Green
Write-Host "========================================================"
Write-Host ""

$gitCmd = Get-Command "git" -ErrorAction SilentlyContinue

if (-not $gitCmd) {
    Write-Host "[BILGI] 'git' komutu ortam değişkenlerinde (PATH) bulunamadı." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Alternatif Yöntem:" -ForegroundColor Cyan
    Write-Host "1. https://github.com/new adresine gidip 'oviscan-android' adında bir repo oluşturun."
    Write-Host "2. 'oviscan-android-release.zip' arşivini açıp dosyaları GitHub Desktop, Git Bash veya web arayüzünden yükleyin."
    Write-Host "3. Repo Settings -> Pages bölümünden Source olarak 'GitHub Actions' seçin."
    Write-Host "4. 'https://<kullanici-adiniz>.github.io/oviscan-android/' adresinden canlı demo yayınlanacaktır."
    exit
}

Write-Host "[1/4] Git yerel reposu başlatılıyor..." -ForegroundColor Gray
git init

Write-Host "[2/4] Dosyalar ekleniyor..." -ForegroundColor Gray
git add .

Write-Host "[3/4] Commit oluşturuluyor..." -ForegroundColor Gray
git commit -m "feat: Initial commit - OviScan Android app & GitHub Pages interactive demo"

Write-Host "[4/4] Dal 'main' yapılıyor..." -ForegroundColor Gray
git branch -M main

Write-Host ""
Write-Host "Yerel depo hazır! GitHub üzerinde repo oluşturduktan sonra:" -ForegroundColor Green
Write-Host "git remote add origin https://github.com/<KULLANICI_ADINIZ>/oviscan-android.git" -ForegroundColor Yellow
Write-Host "git push -u origin main" -ForegroundColor Yellow
