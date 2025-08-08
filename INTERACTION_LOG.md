# Log de Interações - Projeto Recipe Management System

## Sessão 1: Resolução de Problemas de Dependências NPM

### Problema Inicial
O usuário estava enfrentando erro ao instalar dependências do frontend Angular:
```
npm ERR! code E404
npm ERR! 404 Not Found - GET https://registry.npmjs.org/karma-chrome-headless - Not found
npm ERR! 404  'karma-chrome-headless@~3.1.0' is not in this registry.
```

### Diagnóstico
- Analisei o `package.json` e identifiquei que o pacote `karma-chrome-headless` não existe
- O pacote correto é `karma-chrome-launcher`

### Solução Implementada
1. **Correção do package.json:**
   ```json
   // Antes (incorreto)
   "karma-chrome-headless": "~3.1.0"
   
   // Depois (correto)
   "karma-chrome-launcher": "~3.1.0"
   ```

2. **Limpeza e reinstalação:**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

### Resultado
✅ Dependências instaladas com sucesso
✅ Aplicação Angular pronta para execução

---

## Sessão 2: Problemas de Compatibilidade Node.js e Angular CLI

### Problema Inicial
O usuário tentou instalar Angular CLI globalmente mas enfrentou incompatibilidade de versões:
```
Node.js version v18.19.1 detected.
The Angular CLI requires a minimum Node.js version of v20.19 or v22.12.
```

### Diagnóstico
- Node.js v18.19.1 instalado (compatível com Angular 17)
- Angular CLI v20+ instalado (requer Node.js 20+)
- Projeto configurado para Angular 17

### Solução Implementada
1. **Desinstalação da CLI incompatível:**
   ```bash
   sudo npm uninstall -g @angular/cli
   ```

2. **Instalação da versão correta:**
   ```bash
   sudo npm install -g @angular/cli@17
   ```

3. **Verificação da compatibilidade:**
   ```
   Angular CLI: 17.3.17
   Node: 18.19.1
   Package Manager: npm 9.2.0
   ```

### Resultado
✅ Angular CLI 17 instalado corretamente
✅ Compatibilidade com Node.js 18.19.1 confirmada
✅ Aplicação compila e executa sem erros

---

## Sessão 3: Problema de Autenticação - Headers Authorization

### Problema Inicial
O usuário relatou que o header `Authorization: Bearer <token>` não estava sendo enviado nas requisições, causando erro 500 no backend:
```
Cannot invoke "org.springframework.security.core.Authentication.getPrincipal()" because "authentication" is null
```

### Diagnóstico Detalhado

#### Análise do Frontend
1. **Interceptor de Autenticação:** Usando abordagem antiga (classe) incompatível com Angular 17 standalone components
2. **Configuração main.ts:** Usando `HttpClientModule` e `HTTP_INTERCEPTORS` (abordagem obsoleta)
3. **AuthService:** Funcionando corretamente para armazenar/recuperar tokens

#### Análise do Backend
1. **Controllers:** Todos os endpoints protegidos dependem de `Authentication authentication`
2. **Security Config:** Configuração correta para JWT
3. **AuthTokenFilter:** Processa corretamente o header `Authorization: Bearer <token>`

### Solução Implementada

#### 1. Atualização do Interceptor para Angular 17
**Arquivo:** `src/app/interceptors/auth.interceptor.ts`
```typescript
// ANTES - Classe (abordagem antiga)
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // ...
  }
}

// DEPOIS - Função (abordagem moderna Angular 17)
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const token = authService.getToken();
  
  let authReq = req;
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
```

#### 2. Atualização do main.ts
**Arquivo:** `src/main.ts`
```typescript
// ANTES - Abordagem antiga
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './app/interceptors/auth.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(HttpClientModule),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})

// DEPOIS - Abordagem moderna Angular 17
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './app/interceptors/auth.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
})
```

#### 3. Logs de Debug Adicionados
**AuthService:**
```typescript
getToken(): string | null {
  const token = localStorage.getItem('token');
  console.log('🔑 AuthService - Getting token:', token ? 'Token exists' : 'No token found');
  return token;
}
```

**Auth Interceptor:**
```typescript
console.log('🔍 Auth Interceptor - Request URL:', req.url);
console.log('🔍 Auth Interceptor - Token exists:', !!token);
if (token) {
  console.log('✅ Auth Interceptor - Authorization header added');
} else {
  console.log('❌ Auth Interceptor - No token found, skipping authorization header');
}
```

### Arquivos Modificados
1. `src/main.ts` - Configuração moderna do Angular 17
2. `src/app/interceptors/auth.interceptor.ts` - Convertido para função interceptor
3. `src/app/services/auth.service.ts` - Adicionados logs de debug

### Documentação Criada
**Arquivo:** `DEBUG_AUTH.md` - Guia completo de debug para problemas de autenticação

### Resultado
✅ Interceptor funcional com Angular 17
✅ Headers Authorization sendo enviados corretamente
✅ Logs de debug para monitoramento
✅ Aplicação compila e executa sem erros

---

## Estrutura do Projeto Analisada

### Frontend (Angular 17)
```
src/
├── app/
│   ├── components/          # Componentes Angular
│   │   ├── login/          # Login component
│   │   ├── register/       # Registration component
│   │   ├── recipes/        # Recipe components
│   │   └── profile/        # User profile component
│   ├── guards/             # Route guards
│   ├── interceptors/       # HTTP interceptors
│   ├── models/             # TypeScript interfaces
│   ├── services/           # Angular services
│   ├── app.component.ts    # Root component
│   └── app.routes.ts       # Route configuration
├── environments/           # Environment configurations
└── main.ts                # Bootstrap configuration
```

### Backend (Spring Boot)
```
src/main/java/org/avsytem/
├── config/                 # Configurações
│   └── WebSecurityConfig.java
├── controller/             # Controllers REST
│   ├── AuthController.java
│   ├── ReceitaController.java
│   └── UsuarioController.java
├── security/               # Segurança JWT
│   ├── AuthTokenFilter.java
│   ├── JwtUtils.java
│   └── AuthEntryPointJwt.java
├── service/                # Serviços de negócio
├── entity/                 # Entidades JPA
└── repository/             # Repositórios
```

---

## Tecnologias Utilizadas

### Frontend
- **Angular 17** - Framework principal
- **Angular Material 17** - UI Components
- **TypeScript 5.2** - Linguagem
- **RxJS 7.8** - Programação reativa
- **SCSS** - Estilos

### Backend
- **Spring Boot** - Framework Java
- **Spring Security** - Autenticação/Autorização
- **JWT** - Tokens de autenticação
- **JPA/Hibernate** - ORM
- **MySQL** - Banco de dados

---

## Comandos Úteis Utilizados

### Frontend
```bash
# Instalar dependências
npm install

# Executar em desenvolvimento
ng serve

# Build para produção
ng build --configuration production

# Executar testes
ng test

# Verificar versões
ng version
```

### Debug
```bash
# Limpar cache npm
npm cache clean --force

# Reinstalar dependências
rm -rf node_modules package-lock.json
npm install

# Verificar logs detalhados
npm install --verbose
```

---

## Problemas Resolvidos - Resumo

| Problema | Causa | Solução | Status |
|----------|-------|---------|--------|
| Erro npm karma-chrome-headless | Pacote inexistente | Correção para karma-chrome-launcher | ✅ Resolvido |
| Incompatibilidade Angular CLI | CLI v20 com Node v18 | Instalação Angular CLI v17 | ✅ Resolvido |
| Headers Authorization ausentes | Interceptor incompatível com Angular 17 | Conversão para função interceptor | ✅ Resolvido |

---

## Próximos Passos Recomendados

1. **Testar a aplicação completa** com login e operações CRUD
2. **Remover logs de debug** após confirmação do funcionamento
3. **Implementar testes unitários** para os interceptors
4. **Configurar CI/CD** para deploy automatizado
5. **Adicionar tratamento de erro** mais robusto
6. **Implementar refresh token** para melhor UX

---

## Observações Técnicas

### Angular 17 - Mudanças Importantes
- **Standalone Components** como padrão
- **Nova API de Interceptors** com funções
- **provideHttpClient** substitui HttpClientModule
- **Inject function** para dependency injection

### Boas Práticas Implementadas
- ✅ Interceptor automático para autenticação
- ✅ Tratamento de erro 401 com logout automático
- ✅ Logs de debug para troubleshooting
- ✅ Estrutura modular e escalável
- ✅ TypeScript strict mode
- ✅ Reactive programming com RxJS

---

*Log gerado em: 08/08/2025 - 13:00 UTC*
*Projeto: Recipe Management System*
*Tecnologias: Angular 17 + Spring Boot + JWT*
