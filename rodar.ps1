# Lê o .env, cria as variáveis de ambiente e liga o back-end
Get-Content .env | Where-Object { $_ -match '^\s*[^#\s][^=]*=' } | ForEach-Object {
    $nome, $valor = $_ -split '=', 2   # separo só no primeiro "=", porque a senha pode ter outros
    Set-Item -Path "env:$($nome.Trim())" -Value $valor.Trim()
}
.\mvnw spring-boot:run