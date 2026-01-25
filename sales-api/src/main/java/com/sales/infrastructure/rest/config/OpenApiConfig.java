package com.sales.infrastructure.rest.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "Vendas API",
        version = "1.0.0",
        description = """
            API REST para gestão completa de sales, clientes e produtos.

            Desenvolvida com Quarkus seguindo arquitetura hexagonal (Ports and Adapters),
            princípios SOLID e TDD.

            ## Funcionalidades:
            - 🧑‍💼 Gestão completa de clientes (CRUD)
            - 📦 Gestão de produtos com controle de estoque
            - 🛒 Registro de sales com múltiplos itens
            - ✅ Validações completas (CPF, CEP, Email, etc.)
            - 💰 Múltiplas formas de pagamento
            - 📊 Controle de estoque automático

            ## Tecnologias:
            - Java 21
            - Quarkus 3.17.5
            - PostgreSQL
            - Hibernate ORM with Panache
            """,
        contact = @Contact(
            name = "Equipe Vendas API",
            email = "contato@sales.com.br",
            url = "https://github.com/sales-api"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Desenvolvimento"),
        @Server(url = "https://api.sales.com.br", description = "Produção")
    },
    tags = {
        @Tag(name = "Clientes", description = "Operações de gestão de clientes"),
        @Tag(name = "Produtos", description = "Operações de gestão de produtos e estoque"),
        @Tag(name = "Vendas", description = "Operações de registro e consulta de sales")
    }
)
public class OpenApiConfig extends Application {
}
