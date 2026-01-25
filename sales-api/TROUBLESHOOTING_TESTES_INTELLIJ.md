# 🔧 Troubleshooting - Erros no IntelliJ

## 📋 Problemas Reportados

1. ❌ SearchCustomersUseCaseTest
2. ❌ GetDashboardChartDataUseCaseTest
3. ❌ RootResourceTest - shouldReturnRootInformation

---

## 🔍 Possíveis Causas e Soluções

### Problema 1: Dependências Não Carregadas

**Sintoma**: Imports marcados em vermelho, classes não encontradas

**Solução**:

```xml
1. No IntelliJ, abra o painel Maven (View → Tool Windows → Maven)
2. Clique com botão direito no projeto "sales-api"
3. Selecione "Reload Project" ou "Reimport"
4. Aguarde download de todas as dependências
```

Ou via terminal:
```powershell
cd sales-api
mvn dependency:resolve
mvn clean compile
```

---

### Problema 2: IntelliJ Não Reconhece Test Sources

**Sintoma**: Pasta `src/test/java` não está marcada como "Test Sources Root"

**Solução**:

1. Botão direito em `src/test/java`
2. **Mark Directory as** → **Test Sources Root**
3. Reconstruir projeto (Build → Rebuild Project)

---

### Problema 3: Lombok Não Configurado

**Sintoma**: Erros em `@Builder`, `@Getter`, `@Setter`

**Solução**:

1. Instalar plugin Lombok:
   - File → Settings → Plugins
   - Pesquisar "Lombok"
   - Install e Restart IDE

2. Habilitar Annotation Processing:
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - ✅ Enable annotation processing
   - Apply

---

### Problema 4: Versão Incorreta do JDK

**Sintoma**: Erros de sintaxe, métodos não encontrados

**Verificar**:

1. File → Project Structure → Project
2. **Project SDK**: Deve ser Java 21
3. **Project language level**: 21

Se não tiver Java 21:
- Download: https://adoptium.net/temurin/releases/?version=21
- Adicionar no IntelliJ: Add SDK → Download JDK → Version 21

---

### Problema 5: Imports Estáticos Não Resolvidos

**Sintoma**: Métodos como `given()`, `when()`, `assertThat()` marcados em vermelho

**Verificar imports** em cada teste:

**RootResourceTest.java:**
```java
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
```

**SearchCustomersUseCaseTest.java:**
```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
```

**GetDashboardChartDataUseCaseTest.java:**
```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
```

**Solução**: IntelliJ → Code → Optimize Imports (Ctrl+Alt+O)

---

### Problema 6: Quarkus Dev Services Ativo

**Sintoma**: Testes não executam, porta em uso

**Solução**:

Parar processo Quarkus Dev se estiver rodando:
```powershell
# Encontrar processo
Get-Process | Where-Object {$_.ProcessName -like "*java*"}

# Matar processo (substituir PID)
Stop-Process -Id <PID> -Force
```

---

### Problema 7: Cache Corrompido do IntelliJ

**Sintoma**: Erros aleatórios, classes não encontradas mesmo com imports corretos

**Solução**:

1. File → Invalidate Caches
2. Selecionar:
   - ✅ Invalidate and Restart
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS log caches and indexes
3. Clicar em "Invalidate and Restart"

---

## 🧪 Verificação Passo a Passo

### Etapa 1: Limpar e Reconstruir

```powershell
cd C:\Users\jenim\Documents\Projeto\sales-api

# Limpar tudo
mvn clean

# Compilar código principal
mvn compile -DskipTests

# Compilar testes
mvn test-compile
```

**Esperado**: BUILD SUCCESS em todos os passos

---

### Etapa 2: Verificar Classes Específicas

Execute cada comando separadamente e veja qual falha:

```powershell
# Teste 1: SearchCustomersUseCaseTest
mvn test -Dtest=SearchCustomersUseCaseTest

# Teste 2: GetDashboardChartDataUseCaseTest
mvn test -Dtest=GetDashboardChartDataUseCaseTest

# Teste 3: RootResourceTest
mvn test -Dtest=RootResourceTest
```

Se algum falhar, o Maven mostrará o erro exato.

---

### Etapa 3: Verificar Mensagens de Erro Específicas

#### Se o erro for: `cannot find symbol`

**Exemplo**:
```
[ERROR] cannot find symbol: method getFullName()
```

**Causa**: Classe Customer não tem getter ou import errado

**Verificar**:
```java
// Customer.java deve ter:
public String getFullName() {
    return fullName;
}
```

#### Se o erro for: `package does not exist`

**Exemplo**:
```
[ERROR] package com.sales.domain.shared does not exist
```

**Causa**: Dependência faltando ou estrutura de pastas errada

**Solução**:
```powershell
mvn dependency:tree
```

Procurar por conflitos ou dependências faltantes.

#### Se o erro for: `class file has wrong version`

**Exemplo**:
```
[ERROR] class file has wrong version 65.0, should be 61.0
```

**Causa**: JDK versão errada

**Solução**: Configurar JDK 21 (versão 65.0 = Java 21)

---

## 📊 Dependências Necessárias (pom.xml)

Verifique se essas dependências estão no `pom.xml`:

```xml
<dependencies>
    <!-- Testing -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-junit5</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Quarkus Test -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-test-h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🚀 Solução Rápida (Quick Fix)

Execute estes comandos em sequência:

```powershell
cd C:\Users\jenim\Documents\Projeto\sales-api

# 1. Limpar tudo
mvn clean

# 2. Baixar dependências
mvn dependency:resolve

# 3. Compilar
mvn compile

# 4. Compilar testes
mvn test-compile

# 5. Executar os 3 testes problemáticos
mvn test -Dtest=SearchCustomersUseCaseTest,GetDashboardChartDataUseCaseTest,RootResourceTest
```

**Se TODOS passarem**, o problema é no IntelliJ:
- File → Invalidate Caches → Invalidate and Restart

**Se ALGUM falhar**, copie a mensagem de erro completa e me envie.

---

## 📸 Como Copiar o Erro do IntelliJ

1. Abra o teste com erro no IntelliJ
2. Visualize o painel "Problems" (Alt+6)
3. Copie a mensagem de erro completa
4. Ou execute via Maven e copie o output:

```powershell
mvn test -Dtest=NomeDoTeste 2>&1 | Out-File erros.txt
notepad erros.txt
```

---

## ✅ Checklist de Verificação

- [ ] Java 21 instalado e configurado
- [ ] Maven consegue baixar dependências (mvn dependency:resolve)
- [ ] src/test/java marcado como Test Sources Root
- [ ] Plugin Lombok instalado no IntelliJ
- [ ] Annotation Processing habilitado
- [ ] Cache do IntelliJ limpo (Invalidate Caches)
- [ ] Projeto recarregado no Maven (Reload Project)
- [ ] Build limpo (mvn clean compile)
- [ ] Testes compilam (mvn test-compile)

---

## 🆘 Se Nada Funcionar

Execute e me envie o resultado:

```powershell
cd C:\Users\jenim\Documents\Projeto\sales-api

# Informações do ambiente
java -version > diagnostico.txt
mvn -version >> diagnostico.txt

# Dependências
mvn dependency:tree >> diagnostico.txt

# Tentar compilar
mvn clean compile 2>&1 >> diagnostico.txt

# Tentar compilar testes
mvn test-compile 2>&1 >> diagnostico.txt

# Abrir arquivo
notepad diagnostico.txt
```

Copie todo o conteúdo e me envie para análise detalhada.

---

**Última atualização**: 25/01/2026
