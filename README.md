# Projeto Simbioff

## Descrição

Projeto para manutenção dos day offs dos colaboradores da Simbiose..

## Configurações necessárias

- JDK 17 (OpenJDK)
- Node 17
- Npm 8
- Maven 3+
- Postgres 15 ([link](https://hub.docker.com/_/postgres) para como instalá-lo utilizando o Docker)
- Insomnia ([link](https://insomnia.rest/) para a página de como instalá-lo)

## Executando o projeto

### Configurando o Postgres

- Entre dentro do postgres(utilizando a linha de comando ou o PgAdmin) e crie um banco de dados chamado **simbioff**

### Backend

- Entrar no root do projeto e executar o comando:
```shell
$ mvn install -DskipTests
```

- Rodar o seguinte comando para iniciar a API
```shell
$ mvn org.springframework.boot:spring-boot-maven-plugin:run
```

- Se tudo estiver rodando corretamente, ao final você verá uma mensagem apontando para o link da documentação no teu ambiente local:
  - `Documentation :  http://localhost:8080/swagger-ui/index.html#`

### Frontend

- Somente execute os passos abaixo se tiver completado com sucesso os passos anteriores da seção de [Configurando o Postgres](#configurando-o-postgres) e da seção de [Backend](#backend)

- Entre dentro da pasta `simbioffFrontEnd/webapp`

- Rode o seguinte comando no terminal para baixar as dependências:
```shell
$ npm install
```

- Rode o seguinte comando no terminal para executar a parte de frontend:
```shell
$ npm start
```

- Se tudo tiver ocorrido com sucesso, você será direcionado para a página inicial do projeto

![image](https://user-images.githubusercontent.com/47724385/202561850-5d04587c-bc86-4557-a42f-caf343141e34.png)

## Configurando as ferramentas

### Insomnia

- Após rodar a API de Backend, clique no link da documentação que aparece ao fim do projeto: `Documentation :  http://localhost:8080/swagger-ui/index.html#`

- Você será redirecionado para a página de documentação da API do projeto. Clique no **/api-docs** que está abaixo do título **OpenAPI Definition**:

![image](https://user-images.githubusercontent.com/47724385/202564121-ecca3e6f-ad34-46a6-8d17-1c0290344c13.png)

- Irá aparecer um texto em formato json. Copie este texto.

- Abra o Insomnia

- Para importar o projeto no Insomnia. Clique no link da coleção/projeto que você está e selecione a opção **Import/Export**:

![image](https://user-images.githubusercontent.com/47724385/202564197-d4c434da-a802-4458-91b2-77586e2e24f5.png)

- Vá na aba **Data** e clique em **Import Data**

![image](https://user-images.githubusercontent.com/47724385/202564254-4a2d9675-06e7-4cf9-86e4-e990fca0de11.png)

- Selecione a opção **From Clipboard**

- Ele irá automaticamente carregar as requisições para o Insomnia se tudo der certo e a imagem que aparecerá no canto direito do programa é similar a esta:

![image](https://user-images.githubusercontent.com/47724385/202564321-a3a58003-4bd4-455a-a8ea-76d928bc4f7c.png)
