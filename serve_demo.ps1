# Local Web Server for OviScan Demo
$port = 8085
$folder = "C:\Users\murat.erman\.gemini\antigravity\scratch\oviscan-android\docs"

$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add("http://localhost:$port/")
$listener.Start()

Write-Host "=========================================================="
Write-Host " OviScan Prototip & Karar Destek Sunucusu Başlatıldı"
Write-Host " Adres: http://localhost:$port/"
Write-Host "=========================================================="

Start-Process "http://localhost:$port/"

while ($listener.IsListening) {
    try {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response

        $urlPath = $request.Url.LocalPath
        if ($urlPath -eq "/" -or $urlPath -eq "") {
            $urlPath = "/index.html"
        }

        $filePath = Join-Path $folder $urlPath.TrimStart('/')

        if (Test-Path $filePath -PathType Leaf) {
            $bytes = [System.IO.File]::ReadAllBytes($filePath)
            if ($filePath.EndsWith(".html")) {
                $response.ContentType = "text/html; charset=utf-8"
            } elseif ($filePath.EndsWith(".js")) {
                $response.ContentType = "application/javascript"
            } elseif ($filePath.EndsWith(".css")) {
                $response.ContentType = "text/css"
            } else {
                $response.ContentType = "application/octet-stream"
            }
            $response.ContentLength64 = $bytes.Length
            $response.OutputStream.Write($bytes, 0, $bytes.Length)
        } else {
            $response.StatusCode = 404
            $buf = [System.Text.Encoding]::UTF8.GetBytes("404 Not Found")
            $response.OutputStream.Write($buf, 0, $buf.Length)
        }
        $response.Close()
    } catch {
        # Continue loop on client disconnect
    }
}
