# Unisolar 🌞 - Backend

## 🌍 Visão Geral

O projeto Unisolar visa otimizar o uso de energia renovável, utilizando painéis solares, baterias de carros elétricos reutilizadas e um sistema baseado em IA para maximizar a eficiência no consumo e armazenamento de energia.
A solução oferece uma maneira inteligente de aproveitar a energia solar, proporcionando uma alternativa mais barata e sustentável para pessoas de baixa renda. Além disso, o projeto busca firmar parcerias com o governo, oferecendo benefícios fiscais para empresas que doem ou concedem descontos em prol dessa causa, contribuindo para a inclusão social e ambiental.

## 🛠 Pré-requisitos

- Java 17
- Maven para gerenciamento de dependências
- PostgreSQL como banco de dados

## 🚀 Rodando a Aplicação Localmente

### Passos de Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/evamyuu/unisolar-api
   ```

2. Instale as dependências:
   ```bash
   mvn clean install
   ```

3. Inicie o servidor:
    - Encontre a classe `ApiApplication.java`
    - Execute a classe no seu IDE (recomendado IntelliJ)

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
