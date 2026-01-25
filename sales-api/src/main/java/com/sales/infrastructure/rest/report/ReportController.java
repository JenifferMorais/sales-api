package com.sales.infrastructure.rest.report;

import com.sales.application.report.usecase.GetMonthlyRevenueUseCase;
import com.sales.application.report.usecase.GetNewCustomersUseCase;
import com.sales.application.report.usecase.GetOldestProductsUseCase;
import com.sales.application.report.usecase.GetTopRevenueProductsUseCase;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueRequest;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueResponse;
import com.sales.infrastructure.rest.report.dto.NewCustomersRequest;
import com.sales.infrastructure.rest.report.dto.NewCustomersResponse;
import com.sales.infrastructure.rest.report.dto.OldestProductsResponse;
import com.sales.infrastructure.rest.report.dto.TopRevenueProductsResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Relatórios", description = "Endpoints para relatórios gerenciais")
public class ReportController {

    private final GetMonthlyRevenueUseCase getMonthlyRevenueUseCase;
    private final GetTopRevenueProductsUseCase getTopRevenueProductsUseCase;
    private final GetOldestProductsUseCase getOldestProductsUseCase;
    private final GetNewCustomersUseCase getNewCustomersUseCase;

    @Inject
    public ReportController(GetMonthlyRevenueUseCase getMonthlyRevenueUseCase,
                           GetTopRevenueProductsUseCase getTopRevenueProductsUseCase,
                           GetOldestProductsUseCase getOldestProductsUseCase,
                           GetNewCustomersUseCase getNewCustomersUseCase) {
        this.getMonthlyRevenueUseCase = getMonthlyRevenueUseCase;
        this.getTopRevenueProductsUseCase = getTopRevenueProductsUseCase;
        this.getOldestProductsUseCase = getOldestProductsUseCase;
        this.getNewCustomersUseCase = getNewCustomersUseCase;
    }

    @POST
    @Path("/monthly-revenue")
    @Operation(summary = "Relatório de Faturamento Mensal",
               description = "Exibe o faturamento dos últimos 12 meses a partir de uma data de referência")
    public Response getMonthlyRevenue(@Valid MonthlyRevenueRequest request) {
        try {
            MonthlyRevenueResponse response = getMonthlyRevenueUseCase.execute(request.getReferenceDate());
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao gerar relatório: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/top-revenue-products")
    @Operation(summary = "Relatório de Maior Faturamento",
               description = "Exibe os 4 produtos que mais trouxeram faturamento desde o início dos registros")
    public Response getTopRevenueProducts() {
        try {
            TopRevenueProductsResponse response = getTopRevenueProductsUseCase.execute();
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao gerar relatório: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/oldest-products")
    @Operation(summary = "Relatório de Produtos Encalhados",
               description = "Exibe os 3 produtos mais antigos cadastrados, ordenados do mais caro para o mais barato")
    public Response getOldestProducts() {
        try {
            OldestProductsResponse response = getOldestProductsUseCase.execute();
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao gerar relatório: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/new-customers")
    @Operation(summary = "Relatório de Novos Clientes",
               description = "Exibe clientes cadastrados em um determinado ano")
    public Response getNewCustomers(@Valid NewCustomersRequest request) {
        try {
            NewCustomersResponse response = getNewCustomersUseCase.execute(request.getYear());
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao gerar relatório: " + e.getMessage()))
                    .build();
        }
    }
}
