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

As tentativas realizadas permitiram compreender melhor o processo de utilização da ferramenta, evidenciando sua eficácia para apoiar atividades cotidianas e promover maior eficiência. Contudo, no contexto de conversão de projetos, observou-se que a realização manual, aliada a recursos de conversão, correção e geração de código fornecidos pelo Amazon Q, mostra-se mais adequada. Essa combinação torna o trabalho mais ágil e eficiente, especialmente dentro da realidade analisada.

Verificou-se que, independentemente da qualidade da conversão automática, quase sempre há necessidade de adaptações ou de instruções técnicas adicionais para direcionar corretamente a ferramenta. Nesse sentido, a aprendizagem de tecnologias complementares, como Angular, revelou-se relevante para compreender os problemas e efetuar ajustes no código gerado.

Constatou-se também que a utilização do Amazon Q apresenta benefícios adicionais no aspecto da segurança e da governança de código, uma vez que evita o armazenamento de informações sensíveis em contas pessoais ou ambientes externos à organização. Para fins de desenvolvimento, a ferramenta mostrou-se bastante útil; entretanto, no caso da migração entre tecnologias, não foi possível concluir que a conversão totalmente automatizada seja superior à abordagem manual com suporte pontual da inteligência artificial.
