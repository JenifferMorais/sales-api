package com.sales.infrastructure.rest.product.controller;

import com.sales.application.product.usecase.*;
import com.sales.domain.product.entity.Product;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.rest.common.PageResponse;
import com.sales.infrastructure.rest.product.dto.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.logging.Logger;
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

@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Produtos", description = "API para gestão de produtos e controle de estoque")
public class ProductController {

    private static final Logger LOG = Logger.getLogger(ProductController.class);

    @Inject CreateProductUseCase createProductUseCase;
    @Inject UpdateProductUseCase updateProductUseCase;
    @Inject FindProductUseCase findProductUseCase;
    @Inject DeleteProductUseCase deleteProductUseCase;
    @Inject SearchProductsUseCase searchProductsUseCase;
    @Inject ProductMapper mapper;

    @POST
    @Operation(
        summary = "Criar novo produto",
        description = "Cadastra um novo produto no sistema com controle de estoque"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Produto criado com sucesso",
            content = @Content(
                schema = @Schema(implementation = ProductResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "code": "PROD001",
                      "name": "Batom Matte Vermelho",
                      "type": "LIPS",
                      "details": "Batom de longa duração com acabamento matte",
                      "weight": 0.25,
                      "purchasePrice": 15.00,
                      "salePrice": 35.00,
                      "height": 10.0,
                      "width": 2.5,
                      "depth": 2.5,
                      "destinationVehicle": "Todos os tipos de pele",
                      "stockQuantity": 0,
                      "createdAt": "2024-01-20T10:30:00"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Preço de venda não pode ser menor que o preço de compra"
                    }
                    """
                )
            )
        )
    })
    public Response create(@Valid ProductRequest request) {
        LOG.infof("Recebida requisição para criar produto - Nome: %s, Tipo: %s",
                  request.getName(), request.getType());

        Product product = mapper.toDomain(request);
        Product created = createProductUseCase.execute(product);

        LOG.infof("Produto criado via API - ID: %d, Código: %s",
                  created.getId(), created.getCode());

        return Response.status(Response.Status.CREATED).entity(mapper.toResponse(created)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Atualizar produto",
        description = "Atualiza as informações de um produto existente"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @APIResponse(responseCode = "404", description = "Produto não encontrado"),
        @APIResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Response update(
        @Parameter(description = "ID do produto", required = true, example = "1")
        @PathParam("id") Long id,
        @Valid ProductRequest request
    ) {
        LOG.infof("Recebida requisição para atualizar produto - ID: %d, Nome: %s",
                  id, request.getName());

        Dimensions dimensions = new Dimensions(request.getHeight(), request.getWidth(), request.getDepth());
        Product updated = updateProductUseCase.execute(
                id, request.getName(), ProductType.fromString(request.getType()),
                request.getDetails(), request.getWeight(), request.getPurchasePrice(),
                request.getSalePrice(), dimensions, request.getDestinationVehicle()
        );

        LOG.infof("Produto atualizado via API - ID: %d", id);

        return Response.ok(mapper.toResponse(updated)).build();
    }

    @GET
    @Path("/search")
    @Operation(
        summary = "Buscar produtos com paginação",
        description = "Busca produtos por filtro de texto (nome, código, detalhes, tipo) com paginação"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de produtos retornada com sucesso"
        )
    })
    public Response searchProducts(
        @Parameter(description = "Filtro de busca", example = "Batom")
        @QueryParam("filter") String filter,
        @Parameter(description = "Número da página (começa em 0)", example = "0")
        @QueryParam("page") @DefaultValue("0") int page,
        @Parameter(description = "Tamanho da página", example = "10")
        @QueryParam("size") @DefaultValue("10") int size
    ) {
        PageResult<Product> result = searchProductsUseCase.execute(filter, page, size);
        List<ProductResponse> content = result.getContent().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        PageResponse<ProductResponse> response = new PageResponse<>(
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
        summary = "Buscar produto por ID",
        description = "Retorna os dados completos de um produto"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Produto encontrado",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public Response findById(
        @Parameter(description = "ID do produto", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        Product product = findProductUseCase.findById(id);
        return Response.ok(mapper.toResponse(product)).build();
    }

    @GET
    @Path("/code/{code}")
    @Operation(
        summary = "Buscar produto por código",
        description = "Retorna os dados de um produto pelo código único"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Produto encontrado"),
        @APIResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public Response findByCode(
        @Parameter(description = "Código do produto", required = true, example = "PROD001")
        @PathParam("code") String code
    ) {
        Product product = findProductUseCase.findByCode(code);
        return Response.ok(mapper.toResponse(product)).build();
    }

    @GET
    @Operation(
        summary = "Listar produtos",
        description = "Lista todos os produtos, opcionalmente ordenados alfabeticamente por nome"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de produtos",
            content = @Content(
                schema = @Schema(type = SchemaType.ARRAY, implementation = ProductResponse.class)
            )
        )
    })
    public Response findAll(
        @Parameter(description = "Ordenar por nome (true/false)", example = "true")
        @QueryParam("sorted")
        @DefaultValue("false") boolean sorted
    ) {
        List<Product> products = sorted ? findProductUseCase.findAllSortedByName() : findProductUseCase.findAll();
        List<ProductResponse> response = products.stream().map(mapper::toResponse).collect(Collectors.toList());
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Deletar produto",
        description = "Remove um produto do sistema"
    )
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Produto deletado com sucesso"),
        @APIResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public Response delete(
        @Parameter(description = "ID do produto", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        LOG.infof("Recebida requisição para excluir produto - ID: %d", id);

        deleteProductUseCase.execute(id);

        LOG.infof("Produto excluído via API - ID: %d", id);

        return Response.noContent().build();
    }
}
