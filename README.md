<<<<<<< HEAD
# Flor de Lótus - Backend

Este é o código fonte do projeto Flor de Lótus, uma aplicação de gerenciamento veterinário desenvolvida em Java utilizando Spring Boot.

## Pré-requisitos

- *Java 17* ou superior
- *Maven* para gerenciamento de dependências para o back-end
- *PostgreSQL* como banco de dados
- *Yarn* como gerenciador de dependências para o front-end

## Configuração do Banco de Dados

1. Instale e configure o PostgreSQL na sua máquina.
2. Crie um banco de dados com o nome vet_flordelotus_api.
3. Atualize o arquivo application.properties com as credenciais do seu banco de dados PostgreSQL.

Exemplo de configuração no application.properties:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vet_flordelotus_api
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=true



## Rodando a Aplicação

### Passos para rodar localmente

1. *Clone o repositório:*

   bash
git clone https://github.com/seu-usuario/flor-de-lotus.git
cd flor-de-lotus/api



2. *Instale as dependências:*

   Navegue até a pasta raiz do projeto e execute:

   bash
mvn clean install 
yarn 




3. **Inicie o servidor:**

   Utilize o seguinte comando para iniciar a aplicação:

   bash
mvn spring-boot:run
yarn dev 



   A aplicação estará disponível em [http://localhost:8080](http://localhost:8080).

## Documentação da API

A documentação da API é gerada automaticamente utilizando Swagger. Acesse [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) para visualizar e testar os endpoints dispon veis.

## Testes

Para executar os testes da aplicação, utilize o comando:

bash
mvn test
```


## Contribuindo

1. Faça um fork do projeto
2. Crie uma nova branch (git checkout -b feature/nova-feature)
3. Faça o commit das suas mudan as (git commit -am 'Adiciona nova feature')
4. Faça o push para a branch (git push origin feature/nova-feature)
5. Crie um novo Pull Request

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
=======
# unisolar-api
>>>>>>> fb6412f15b21d2f24480012672d6e20edc534925
