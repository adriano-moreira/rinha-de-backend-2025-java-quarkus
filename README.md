# rinha-de-backend-2025-java-quarkus

Projeto para participar da 🐓[rinha-de-backend-2025](https://github.com/zanfranceschi/rinha-de-backend-2025)

```mermaid
---
title: Overview Arquitetura
---
flowchart LR
    G(Stress Test - K6) -.-> LB(Load Balancer - Nginx)
    subgraph App Network
        LB -.-> API1(API - instância 01 - Quarkus)
        LB -.-> API2(API - instância 02 - Quarkus)
        API1 -.-> Db[(Postgres)]
        API2 -.-> Db
        Db -.-> Worker1(Worker - Quarkus)
    end
        Worker1 -.-> Db
        Worker1 -.-> DefaultProcessor
        Worker1 -.-> FallbackProcessor
    subgraph Processor Network
        DefaultProcessor
        FallbackProcessor
    end
```
### PGConf Brasil 2022 - Implementando Mensageria com PostgreSQL, por Rafael Ponte
[![PGConf Brasil 2022 - Implementando Mensageria com PostgreSQL, por Rafael Ponte](http://img.youtube.com/vi/jTLP5DrIocA/0.jpg)](https://www.youtube.com/watch?v=jTLP5DrIocA "XXX")

- Quarkus 3.20(current LTS)
- Java 21(current LTS)
- Maven 3


---
# Aprendizangens


