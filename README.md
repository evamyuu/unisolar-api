# Unisolar 🌞 - Backend
![Unisolar Header]()
## 🌍 Visão Geral

O projeto Unisolar visa otimizar o uso de energia renovável, utilizando painéis solares, baterias de carros elétricos reutilizadas e um sistema baseado em IA para maximizar a eficiência no consumo e armazenamento de energia.
A solução oferece uma maneira inteligente de aproveitar a energia solar, proporcionando uma alternativa mais barata e sustentável para pessoas de baixa renda. Além disso, o projeto busca firmar parcerias com o governo, oferecendo benefícios fiscais para empresas que doem ou concedem descontos em prol dessa causa, contribuindo para a inclusão social e ambiental.

## 🛠 Pré-requisitos

- **Java 17**: [Instalar Java 17](https://www.oracle.com/br/java/technologies/downloads/)
- **Maven** (versão 3.6 ou superior): [Instalar Maven](https://maven.apache.org/install.html)
- **PostgreSQL** (versão 12 ou superior): [Instalar PostgreSQL](https://www.postgresql.org/download/)

## 🚀 Rodando a Aplicação Localmente

### Passos de Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/evamyuu/unisolar-api
   ```
   
2. Certifique-se de que o Maven está instalado executando o comando abaixo no terminal:
   ```bash
   mvn -v
   ```

3. Abra o terminal na raiz do projeto (diretório onde o arquivo `pom.xml` está localizado) e execute:
   ```bash
   mvn clean install
   ```
   Isso irá baixar todas as dependências necessárias para o projeto.

4. Após configurar o banco de dados e a SolarIA, siga os passos abaixo para iniciar o servidor:
   
- Localize a classe `ApiApplication.java` no seguinte caminho:
    src/main/java/unisolar/api/ApiApplication.java
   
- Execute a classe para iniciar o servidor (Recomendado IntelliJ IDEA).

## 📦 Configuração do Banco de Dados

1. Instale o PostgreSQL
2. Crie um banco de dados chamado `unisolar_api`
3. Configure o arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/unisolar_api
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.error.include-stacktrace=never
api.security.token.secret=${JWT_SECRET:12345678}

```
## 🔐 Configuração da SolarIA

### Opções de Configuração

⚠️ **IMPORTANTE**: Existem duas formas de utilizar a SolarIA:

#### 1. Configuração com Chave OpenAI

1. Obtenha uma chave de API no site da OpenAI
2. Adicione ao arquivo `application.properties`:

```properties
app.openai.api.key=SUA_CHAVE_OPENAI
app.openai.assistant.id=asst_npBEXmBi86X176hjRqpwToby
```
**Observação**:
A chave OpenAI configurada no nosso arquivo `application.properties` é apenas um exemplo e **não é válida**.

**Notas de Segurança**:
- **NUNCA** compartilhe sua chave de API publicamente
- Adicione `application.properties` ao `.gitignore`
- Use variáveis de ambiente para gerenciar credenciais sensíveis

#### 2. Modo de Demonstração (Sem Chave)

🤖 **Simulação da SolarIA**:
- Caso não possua uma chave OpenAI, o projeto possui um **modo de demonstração**
- Implementamos uma simulação local do funcionamento da IA
- Caso a aplicação não detectar uma chave válida, as respostas serão geradas por um modelo de resposta simulado para perguntas comuns
- **Objetivo**: Permitir que desenvolvedores e usuários testem o sistema completamente

## 🔐 Credenciais Padrão

Após iniciar o servidor, será gerado automaticamente um usuário padrão. Utilize as credenciais abaixo para realizar o login ou, se preferir, crie um novo cadastro.

- **Usuário**: `user`
- **Senha**: `password`
  
## 🖥 Interface CLI

### Menu de Login
1. 🔑 Login
2. 📝 Cadastro

### Menu Principal
1. 🔎 Buscar
2. 📊 Dashboard
3. 👤 Perfil do Usuário
4. 🤖 Chat com SolarIA
5. 🔒 Alterar Senha
6. 🚶‍♂️ Logout
## 📚 Documentação da API

Acesse a documentação Swagger em:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🤝 Como Contribuir

Unisolar é um projeto open source, e a melhor maneira de contribuir é divulgando o projeto. 
Compartilhe com amigos, familiares e nas redes sociais para ajudar a alcançar mais pessoas que possam se beneficiar ou contribuir para o projeto.

## 📞 Suporte e Feedback

- **E-mail**: unisolar.contato@gmail.com
- **GitHub**: Abra uma issue no repositório

## 📄 Licença

Projeto licenciado sob a **Licença MIT**

---

**Desenvolvido com 🧡💚💙 pelo Unisolar Team**
