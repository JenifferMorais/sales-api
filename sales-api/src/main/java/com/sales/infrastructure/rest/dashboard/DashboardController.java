package com.sales.infrastructure.rest.dashboard;

import com.sales.application.dashboard.usecase.GetDashboardChartDataUseCase;
import com.sales.application.dashboard.usecase.GetDashboardStatsUseCase;
import com.sales.application.dashboard.usecase.GetRecentSalesUseCase;
import com.sales.infrastructure.rest.dashboard.dto.DashboardChartResponse;
import com.sales.infrastructure.rest.dashboard.dto.DashboardStatsResponse;
import com.sales.infrastructure.rest.dashboard.dto.RecentSalesResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/v1/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Dashboard", description = "Endpoints para dados do dashboard")
public class DashboardController {

    private final GetDashboardStatsUseCase getDashboardStatsUseCase;
    private final GetDashboardChartDataUseCase getDashboardChartDataUseCase;
    private final GetRecentSalesUseCase getRecentSalesUseCase;

    @Inject
    public DashboardController(
            GetDashboardStatsUseCase getDashboardStatsUseCase,
            GetDashboardChartDataUseCase getDashboardChartDataUseCase,
            GetRecentSalesUseCase getRecentSalesUseCase) {
        this.getDashboardStatsUseCase = getDashboardStatsUseCase;
        this.getDashboardChartDataUseCase = getDashboardChartDataUseCase;
        this.getRecentSalesUseCase = getRecentSalesUseCase;
    }

    @GET
    @Path("/stats")
    @Operation(
            summary = "Obter estatísticas do dashboard",
            description = "Retorna estatísticas agregadas incluindo total de vendas, receita, clientes e produtos"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Estatísticas obtidas com sucesso",
                    content = @Content(schema = @Schema(implementation = DashboardStatsResponse.class))
            ),
            @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response getStats() {
        try {
            DashboardStatsResponse response = getDashboardStatsUseCase.execute();
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao obter estatísticas: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/chart-data")
    @Operation(
            summary = "Obter dados do gráfico do dashboard",
            description = "Retorna dados de vendas e receita para o período especificado (week, month, quarter, year)"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Dados do gráfico obtidos com sucesso",
                    content = @Content(schema = @Schema(implementation = DashboardChartResponse.class))
            ),
            @APIResponse(responseCode = "400", description = "Parâmetro de período inválido"),
            @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response getChartData(
            @Parameter(
                    description = "Período para os dados do gráfico (week, month, quarter, year)",
                    required = false,
                    schema = @Schema(defaultValue = "month")
            )
            @QueryParam("range") @DefaultValue("month") String range) {
        try {
            if (!isValidRange(range)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Período inválido. Use: week, month, quarter ou year"))
                        .build();
            }

            DashboardChartResponse response = getDashboardChartDataUseCase.execute(range);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao obter dados do gráfico: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/recent-sales")
    @Operation(
            summary = "Obter vendas recentes",
            description = "Retorna as vendas mais recentes para exibição no dashboard"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Vendas recentes obtidas com sucesso",
                    content = @Content(schema = @Schema(implementation = RecentSalesResponse.class))
            ),
            @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response getRecentSales(
            @Parameter(
                    description = "Número de vendas recentes a retornar",
                    required = false,
                    schema = @Schema(defaultValue = "5")
            )
            @QueryParam("limit") @DefaultValue("5") int limit) {
        try {
            if (limit <= 0 || limit > 50) {
                limit = 5; // Default to 5 if invalid
            }

            RecentSalesResponse response = getRecentSalesUseCase.execute(limit);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao obter vendas recentes: " + e.getMessage()))
                    .build();
        }
    }

    private boolean isValidRange(String range) {
        return range.equals("week") || range.equals("month") ||
                range.equals("quarter") || range.equals("year");
    }
}
