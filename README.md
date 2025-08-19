# Teste Amazon Q

## Descrição

Este projeto é uma aplicação web desenvolvida com o framework `ExtJS 3.4` e `Java 8`. A aplicação é renderizada em páginas `JSP` e é executada em um servidor `Apache Tomcat 9`. O objetivo é simular um ambiente com tecnologias e versões semelhantes a aplicação principal da empresa para testes de migração e modernização do sistema.

1\. Arvore de arquivos da pasta corrente :
```
C:.
│  .gitignore (Arquivo git pra não rastrear pastas e arquivos que não são exenciais)
│   mvnw       (Aquivo Maven)
│   mvnw.cmd   (Aquivo Maven)
│   pom.xml    (Contém informações sobre o projeto e os detalhes de configuração usados pelo Maven)
│   README.md  (Arquivo de apresentação do projeto)
|
├──.mvn        (Maven Files)
│
├──Files
│       ConfigureDatabase.sql (Tabelas e dados do projeto)
│
├──src (Código fonte Do projeto melhor descrito em Arquitetura do projeto piloto)
```

2\. [Documentação do projeto piloto](./src/DOCUMENTACAO_CODIGO.md)

3\. [Wiki de tentativas](https://github.com/avsysgeo-thiagomota/teste-amazon-q/wiki)

### Onde queremos chegar

A ideia e estudar o processo de conversão/mpdernização de software via Amazon Q com esse programa. Para então aplicar no projeto priciapal da empresa. A aplicação principal da empresa é legada. Tem um padrão de projeto próprio e de descrever grandes e complexos processos.

### Conclusão

Verificou-se que, independentemente da qualidade da conversão automática, quase sempre há necessidade de adaptações manuais para o correto funcionamento ou de instruções específicas para direcionar a ferramenta na implementação. A aprendizagem de tecnologias complementares, como Angular, e saber trabalhar com Spring Boot revelou-se essencial para compreender problemas gerados durante a conversão e efetuar ajustes ou direcionamentos.

Tentar fazer a conversão de todo o projeto diretamente pelo Amazon Q pode não ser a melhor opção. Quanto mais específicos e maior o contexto dos cenários passados, mais difícil é fazer isso. O Amazon Q é melhor com cenários generalistas e mais comuns. No nosso caso, os ajustes manuais e instruções extras de correção podem se tornar o trabalho grande demais. Por isso, uma conversão do web_avgeo manual com grande auxílio do Amazon Q parece ser o cenário ideal. Usá-la para tarefas menores e subtarefas durante a conversão deve ser mais eficiente e menos trabalhoso.

Constatou-se também que a utilização do Amazon Q apresenta benefícios adicionais no aspecto da segurança e da governança de código, uma vez que evita o armazenamento de informações sensíveis em contas pessoais ou ambientes externos à organização. Para fins de desenvolvimento, a ferramenta mostrou-se bastante útil. É uma ferramenta de auxílio para diversos setores da empresa (inclusive o desenvolvimento), que tornará o trabalho mais rápido e mais fácil.
