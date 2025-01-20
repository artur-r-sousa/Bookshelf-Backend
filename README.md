README.md - Backend Spring Boot 📚
Bookshelf Backend

O Bookshelf Backend é o serviço que gerencia as funcionalidades e a lógica de negócios da aplicação Bookshelf, uma livraria online. Ele se comunica com o front-end em Angular e faz uso da API do Google Books para buscar informações sobre livros. Este projeto será transformado gradualmente de uma aplicação monolítica para uma arquitetura baseada em microsserviços.
🔥 Visão Geral

Atualmente, o backend é implementado como uma aplicação monolítica em Spring Boot. Com a evolução do projeto, a arquitetura será transformada para utilizar microsserviços, permitindo uma maior escalabilidade e modularidade.
🏗️ Tecnologias Utilizadas

    Linguagem: Java 17
    Framework: Spring Boot 3
    Banco de Dados: PostgreSQL
    API Externa: Google Books API
    Client HTTP: WebClient (Spring WebFlux)
    Gerenciamento de Dependências: Maven

📋 Funcionalidades Atuais

    Busca de Livros: Integração com a API do Google Books para busca de livros por título, autor ou categoria.
    Listagem de Livros por Categoria: Busca e exibição de livros populares e por categorias.
    Sistema de Logs: Uso de logs para monitoramento e debug.

🚀 Roteiro de Evolução

Abaixo está o cronograma de novas features planejadas para o backend.

🗓️ Versão v1.0
(Concluido)

    Funcionalidade: Sistema de Autenticação com JWT
        Descrição: Implementação de autenticação utilizando Spring Security com tokens JWT para controle de acesso.
        Objetivo: Proteger rotas sensíveis e permitir autenticação segura de usuários.

🗓️ Versão v2.0
(Concluido)

    Funcionalidade: Envio de E-mails
        Descrição: Implementação de um serviço de envio de e-mails para notificações como confirmação de cadastro e recuperação de senha.
        Tecnologia: SendGrid ou JavaMail.
        Objetivo: Facilitar a comunicação com os usuários.

🗓️ Versão v3.0

    Funcionalidade: Implementação de Carrinho de Compras
        Descrição: Implementação de um serviço para gerenciar o carrinho de compras dos usuários.
        Objetivo: Permitir a adição, remoção e atualização de itens no carrinho de compras.

🗓️ Versão v4.0

    Funcionalidade: Conversão para Microsserviços
        Descrição: Separação das funcionalidades do backend em serviços independentes.
        Tecnologia: Spring Cloud, Eureka, API Gateway (Zuul ou Spring Cloud Gateway).
        Objetivo: Escalabilidade, melhor modularidade e manutenção simplificada.

🛠️ Como Rodar o Projeto
Pré-requisitos

    Java 17 ou superior
    Maven
    PostgreSQL

Configuração do Banco de Dados

    Crie um banco de dados PostgreSQL:

CREATE DATABASE bookshelf;

Configure o arquivo application.properties:

    spring.datasource.url=jdbc:postgresql://localhost:5432/bookshelf
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

Rodando o Projeto

    Instale as dependências:

./mvnw clean install

Execute a aplicação:

    ./mvnw spring-boot:run

    O backend estará disponível em: http://localhost:8080

    git add README.md
    git commit -m "docs: adicionar README para o backend Spring Boot"
    git push origin main
