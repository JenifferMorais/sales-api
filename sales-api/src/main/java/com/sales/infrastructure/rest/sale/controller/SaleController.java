package com.sales.infrastructure.rest.sale.controller;

import com.sales.application.sale.usecase.*;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.rest.common.PageResponse;
import com.sales.infrastructure.rest.sale.dto.*;
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

@Path("/api/v1/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Vendas", description = "API para registro e consulta de sales")
public class SaleController {

    @Inject CreateSaleUseCase createSaleUseCase;
    @Inject UpdateSaleUseCase updateSaleUseCase;
    @Inject FindSaleUseCase findSaleUseCase;
    @Inject DeleteSaleUseCase deleteSaleUseCase;
    @Inject SearchSalesUseCase searchSalesUseCase;
    @Inject SaleMapper mapper;

    @POST
    @Operation(
        summary = "Registrar nova venda",
        description = """
            Registra uma nova venda no sistema.

            Funcionalidades:
            - Lista produtos ordenados alfabeticamente
            - Calcula subtotal, imposto (9%) e total automaticamente
            - Calcula troco para pagamento em dinheiro
            - Suporta múltiplas formas de pagamento (dinheiro ou cartão)
            - Quantidade de produtos é infinita (sem controle de estoque)
            """
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Venda registrada com sucesso",
            content = @Content(
                schema = @Schema(implementation = SaleResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "code": "SALE001",
                      "customerCode": "CUST001",
                      "customerName": "João Silva",
                      "sellerCode": "SELLER001",
                      "sellerName": "Carlos Vendedor",
                      "paymentMethod": "CREDIT_CARD",
                      "cardNumber": "**** **** **** 3456",
                      "amountPaid": null,
                      "subtotal": 1000.00,
                      "taxAmount": 90.00,
                      "totalAmount": 1090.00,
                      "change": 0.00,
                      "items": [
                        {
                          "productCode": "PROD001",
                          "productName": "Cadeira Ergonômica",
                          "quantity": 2,
                          "unitPrice": 250.00,
                          "totalPrice": 500.00
                        },
                        {
                          "productCode": "PROD002",
                          "productName": "Mesa de Escritório",
                          "quantity": 1,
                          "unitPrice": 500.00,
                          "totalPrice": 500.00
                        }
                      ],
                      "createdAt": "2024-01-20T14:30:00"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "Cliente não encontrado",
                        value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Cliente não encontrado com código: CUST999"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Produto não encontrado",
                        value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Produto não encontrado com código: PROD999"
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
            description = "Dados da venda a ser registrada",
            example = """
            {
              "code": "SALE001",
              "customerCode": "CUST001",
              "sellerCode": "SELLER001",
              "sellerName": "Carlos Vendedor",
              "paymentMethod": "CREDIT_CARD",
              "cardNumber": "1234567890123456",
              "items": [
                {
                  "productCode": "PROD001",
                  "quantity": 2
                },
                {
                  "productCode": "PROD002",
                  "quantity": 1
                }
              ]
            }
            """
        )
        SaleRequest request
    ) {
        Sale sale = mapper.toDomain(request);
        Sale created = createSaleUseCase.execute(sale);
        return Response.status(Response.Status.CREATED).entity(mapper.toResponse(created)).build();
    }

    @GET
    @Path("/search")
    @Operation(
        summary = "Buscar vendas com paginação",
        description = "Busca vendas por filtro de texto (código, nome do cliente, código do cliente, nome do vendedor) com paginação"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de vendas retornada com sucesso"
        )
    })
    public Response searchSales(
        @Parameter(description = "Filtro de busca", example = "João")
        @QueryParam("filter") String filter,
        @Parameter(description = "Número da página (começa em 0)", example = "0")
        @QueryParam("page") @DefaultValue("0") int page,
        @Parameter(description = "Tamanho da página", example = "10")
        @QueryParam("size") @DefaultValue("10") int size
    ) {
        PageResult<Sale> result = searchSalesUseCase.execute(filter, page, size);
        List<SaleResponse> content = result.getContent().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        PageResponse<SaleResponse> response = new PageResponse<>(
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
        summary = "Buscar venda por ID",
        description = "Retorna os dados completos de uma venda incluindo todos os itens"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Venda encontrada",
            content = @Content(schema = @Schema(implementation = SaleResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "Venda não encontrada")
    })
    public Response findById(
        @Parameter(description = "ID da venda", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        Sale sale = findSaleUseCase.findById(id);
        return Response.ok(mapper.toResponse(sale)).build();
    }

    @GET
    @Path("/code/{code}")
    @Operation(
        summary = "Buscar venda por código",
        description = "Retorna os dados de uma venda pelo código único"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Venda encontrada"),
        @APIResponse(responseCode = "404", description = "Venda não encontrada")
    })
    public Response findByCode(
        @Parameter(description = "Código da venda", required = true, example = "SALE001")
        @PathParam("code") String code
    ) {
        Sale sale = findSaleUseCase.findByCode(code);
        return Response.ok(mapper.toResponse(sale)).build();
    }

    @GET
    @Operation(
        summary = "Listar sales",
        description = "Lista todas as sales ou filtra por código do cliente"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de sales",
            content = @Content(
                schema = @Schema(type = SchemaType.ARRAY, implementation = SaleResponse.class)
            )
        )
    })
    public Response findAll(
        @Parameter(description = "Código do cliente para filtrar (opcional)", example = "CUST001")
        @QueryParam("customerCode") String customerCode
    ) {
        List<Sale> sales = customerCode != null
                ? findSaleUseCase.findByCustomerCode(customerCode)
                : findSaleUseCase.findAll();
        List<SaleResponse> response = sales.stream().map(mapper::toResponse).collect(Collectors.toList());
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Atualizar venda",
            description = """
                Atualiza uma venda existente.

                Permite alterar:
                - Vendedor (código e nome)
                - Forma de pagamento
                - Número do cartão (se aplicável)
                - Valor pago
                - Itens da venda

                NÃO permite alterar:
                - Cliente (código fixo)
                - Data da venda
                """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Venda atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = SaleResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Venda não encontrada"),
            @APIResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Response update(
            @Parameter(description = "ID da venda", required = true, example = "1")
            @PathParam("id") Long id,
            @Valid UpdateSaleRequest request
    ) {

        List<com.sales.domain.sale.entity.SaleItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    var product = mapper.findProductByCode(itemRequest.getProductCode());
                    return new com.sales.domain.sale.entity.SaleItem(
                            product.getCode(),
                            product.getName(),
                            itemRequest.getQuantity(),
                            product.getSalePrice()
                    );
                })
                .collect(Collectors.toList());

        Sale updated = updateSaleUseCase.execute(
                id,
                request.getSellerCode(),
                request.getSellerName(),
                com.sales.domain.sale.valueobject.PaymentMethod.fromString(request.getPaymentMethod()),
                request.getCardNumber(),
                request.getAmountPaid(),
                items
        );

        return Response.ok(mapper.toResponse(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Deletar venda",
            description = """
                Remove uma venda do sistema.

                IMPORTANTE:
                - Ao excluir a venda, o registro de produtos e clientes NÃO é afetado
                - Não há controle de estoque, portanto a quantidade não é devolvida
                """
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Venda deletada com sucesso"),
            @APIResponse(responseCode = "404", description = "Venda não encontrada")
    })
    public Response delete(
            @Parameter(description = "ID da venda", required = true, example = "1")
            @PathParam("id") Long id
    ) {
        deleteSaleUseCase.execute(id);
        return Response.noContent().build();
    }
}
