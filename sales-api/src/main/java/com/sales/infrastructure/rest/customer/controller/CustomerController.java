package com.sales.infrastructure.rest.customer.controller;

import com.sales.application.customer.usecase.*;
import com.sales.domain.customer.entity.Customer;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.rest.common.PageResponse;
import com.sales.infrastructure.rest.customer.dto.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes", description = "API para gestão completa de clientes")
public class CustomerController {

    @Inject CreateCustomerUseCase createCustomerUseCase;
    @Inject UpdateCustomerUseCase updateCustomerUseCase;
    @Inject FindCustomerUseCase findCustomerUseCase;
    @Inject DeleteCustomerUseCase deleteCustomerUseCase;
    @Inject SearchCustomersUseCase searchCustomersUseCase;
    @Inject CustomerMapper mapper;

    @POST
    @Operation(
        summary = "Criar novo cliente",
        description = "Cria um novo cliente no sistema com todas as informações obrigatórias (CPF, endereço, etc.)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Cliente criado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = CustomerResponse.class),
                examples = @ExampleObject(
                    name = "Cliente criado",
                    value = """
                    {
                      "id": 1,
                      "code": "CUST001",
                      "fullName": "João Silva",
                      "motherName": "Maria Silva",
                      "cpf": "123.456.789-09",
                      "rg": "123456789",
                      "address": {
                        "zipCode": "01310-100",
                        "street": "Av. Paulista",
                        "number": "1000",
                        "complement": "Apto 101",
                        "neighborhood": "Bela Vista",
                        "city": "São Paulo",
                        "state": "SP"
                      },
                      "birthDate": "1990-05-15",
                      "cellPhone": "(11) 98765-4321",
                      "email": "joao.silva@email.com",
                      "createdAt": "2024-01-20T10:30:00"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Dados inválidos ou cliente já existe",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                examples = {
                    @ExampleObject(
                        name = "CPF inválido",
                        value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "CPF inválido"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Cliente já existe",
                        value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Cliente com CPF 123.456.789-09 já existe"
                        }
                        """
                    )
                }
            )
        )
    })
    public Response create(
        @Valid
        @Schema(
            description = "Dados do cliente a ser criado",
            required = true,
            example = """
            {
              "code": "CUST001",
              "fullName": "João Silva",
              "motherName": "Maria Silva",
              "cpf": "123.456.789-09",
              "rg": "123456789",
              "address": {
                "zipCode": "01310-100",
                "street": "Av. Paulista",
                "number": "1000",
                "complement": "Apto 101",
                "neighborhood": "Bela Vista",
                "city": "São Paulo",
                "state": "SP"
              },
              "birthDate": "1990-05-15",
              "cellPhone": "(11) 98765-4321",
              "email": "joao.silva@email.com"
            }
            """
        )
        CreateCustomerRequest request
    ) {
        Customer customer = mapper.toDomain(request);
        Customer created = createCustomerUseCase.execute(customer);
        return Response.status(Response.Status.CREATED)
                .entity(mapper.toResponse(created))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Atualizar cliente",
        description = "Atualiza as informações de um cliente existente (não atualiza CPF e código)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Cliente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        ),
        @APIResponse(
            responseCode = "404",
            description = "Cliente não encontrado",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                      "status": 404,
                      "error": "Not Found",
                      "message": "Cliente não encontrado com id: 123"
                    }
                    """
                )
            )
        ),
        @APIResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Response update(
        @Parameter(description = "ID do cliente", required = true, example = "1")
        @PathParam("id") Long id,
        @Valid UpdateCustomerRequest request
    ) {
        Customer updated = updateCustomerUseCase.execute(
                id,
                request.getFullName(),
                request.getMotherName(),
                mapper.toAddress(request.getAddress()),
                request.getBirthDate(),
                request.getCellPhone().replaceAll("[^0-9]", ""),
                request.getEmail()
        );
        return Response.ok(mapper.toResponse(updated)).build();
    }

    @GET
    @Path("/search")
    @Operation(
        summary = "Buscar clientes com paginação",
        description = "Busca clientes por filtro de texto (nome, email, código, CPF, telefone) com paginação"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de clientes retornada com sucesso"
        )
    })
    public Response searchCustomers(
        @Parameter(description = "Filtro de busca", example = "João")
        @QueryParam("filter") String filter,
        @Parameter(description = "Número da página (começa em 0)", example = "0")
        @QueryParam("page") @DefaultValue("0") int page,
        @Parameter(description = "Tamanho da página", example = "10")
        @QueryParam("size") @DefaultValue("10") int size
    ) {
        PageResult<Customer> result = searchCustomersUseCase.execute(filter, page, size);
        List<CustomerResponse> content = result.getContent().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        PageResponse<CustomerResponse> response = new PageResponse<>(
                content,
                result.getPage(),
                result.getSize(),
                result.getTotalElements()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Buscar cliente por ID",
        description = "Retorna os dados completos de um cliente pelo ID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Response findById(
        @Parameter(description = "ID do cliente", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        Customer customer = findCustomerUseCase.findById(id);
        return Response.ok(mapper.toResponse(customer)).build();
    }

    @GET
    @Path("/code/{code}")
    @Operation(
        summary = "Buscar cliente por código",
        description = "Retorna os dados completos de um cliente pelo código único"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Cliente encontrado"),
        @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Response findByCode(
        @Parameter(description = "Código único do cliente", required = true, example = "CUST001")
        @PathParam("code") String code
    ) {
        Customer customer = findCustomerUseCase.findByCode(code);
        return Response.ok(mapper.toResponse(customer)).build();
    }

    @GET
    @Operation(
        summary = "Listar clientes",
        description = "Lista todos os clientes ou filtra por nome"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de clientes",
            content = @Content(
                schema = @Schema(type = SchemaType.ARRAY, implementation = CustomerResponse.class)
            )
        )
    })
    public Response findAll(
        @Parameter(description = "Nome para filtrar (opcional)", example = "João")
        @QueryParam("name") String name
    ) {
        List<Customer> customers;
        if (name != null && !name.isBlank()) {
            customers = findCustomerUseCase.findByNameContaining(name);
        } else {
            customers = findCustomerUseCase.findAll();
        }
        List<CustomerResponse> response = customers.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Deletar cliente",
        description = "Remove um cliente do sistema"
    )
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Response delete(
        @Parameter(description = "ID do cliente", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        deleteCustomerUseCase.execute(id);
        return Response.noContent().build();
    }
}
