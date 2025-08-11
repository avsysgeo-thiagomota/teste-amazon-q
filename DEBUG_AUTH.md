# Guia de Debug - Problemas de Autenticação

## Problema Identificado
O header `Authorization: Bearer <token>` não está sendo enviado em algumas requisições, causando erro 500 no backend com `authentication is null`.

## Correções Implementadas

### 1. Atualização do Interceptor para Angular 17
- ✅ Convertido de classe para função interceptor (abordagem moderna do Angular 17)
- ✅ Atualizado `main.ts` para usar `provideHttpClient(withInterceptors([authInterceptor]))`
- ✅ Adicionados logs de debug para monitoramento

### 2. Logs de Debug Adicionados
- 🔍 Interceptor mostra quando token existe e quando header é adicionado
- 🔑 AuthService mostra quando token é recuperado do localStorage
- 💾 AuthService mostra quando token é salvo no login

## Como Testar

### 1. Abrir o Console do Navegador
```bash
# Iniciar o frontend
cd "/mnt/c/Users/Thiago Mota/Documents/GitHub/teste-amazon-q/frontend"
ng serve
```

### 2. Verificar Logs no Console
Ao fazer login e navegar pela aplicação, você deve ver:
- `🔑 AuthService - Getting token: Token exists` (ou "No token found")
- `🔍 Auth Interceptor - Request URL: http://localhost:8080/api/...`
- `🔍 Auth Interceptor - Token exists: true/false`
- `✅ Auth Interceptor - Authorization header added` (quando token existe)

### 3. Verificar Network Tab
No DevTools > Network:
- Verifique se as requisições para `/api/receitas`, `/api/usuarios/me`, etc. têm o header `Authorization: Bearer <token>`

## Possíveis Causas do Problema

### 1. Token Não Está Sendo Salvo
**Sintoma**: Logs mostram "No token found" após login
**Solução**: Verificar se o backend está retornando o token corretamente

### 2. Token Expirado
**Sintoma**: Token existe mas requisições falham com 401
**Solução**: Implementar refresh token ou fazer novo login

### 3. Interceptor Não Está Sendo Aplicado
**Sintoma**: Não aparecem logs do interceptor
**Solução**: Verificar se `main.ts` está configurado corretamente

### 4. Problema de CORS
**Sintoma**: Requisições OPTIONS falham
**Solução**: Verificar configuração CORS no backend

## Comandos de Teste

### Verificar se Token Está no LocalStorage
```javascript
// No console do navegador
console.log('Token:', localStorage.getItem('token'));
console.log('User:', localStorage.getItem('user'));
```

### Testar Requisição Manual
```javascript
// No console do navegador
fetch('http://localhost:8080/api/receitas', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token'),
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log('Success:', data))
.catch(error => console.error('Error:', error));
```

## Próximos Passos

1. **Testar a aplicação** com os logs de debug
2. **Verificar se o token está sendo enviado** nas requisições
3. **Se o problema persistir**, verificar:
   - Se o backend está rodando corretamente
   - Se a configuração CORS está correta
   - Se o token JWT não está expirado

## Remoção dos Logs de Debug

Após resolver o problema, remover os `console.log` dos arquivos:
- `src/app/interceptors/auth.interceptor.ts`
- `src/app/services/auth.service.ts`
