package com.sales.infrastructure.rest.auth.controller;

import com.sales.application.auth.usecase.*;
import com.sales.domain.auth.entity.User;
import com.sales.infrastructure.rest.auth.dto.*;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação", description = "Endpoints para autenticação, registro e recuperação de senha")
public class AuthController {

    private static final Logger LOG = Logger.getLogger(AuthController.class);

    @Inject RegisterUserUseCase registerUserUseCase;
    @Inject LoginUseCase loginUseCase;
    @Inject ForgotPasswordUseCase forgotPasswordUseCase;
    @Inject ResetPasswordUseCase resetPasswordUseCase;
    @Inject LogoutUseCase logoutUseCase;

    @ConfigProperty(name = "jwt.expiration.hours", defaultValue = "24")
    Long jwtExpirationHours;

    @POST
    @Path("/register")
    @PermitAll
    @Operation(
            summary = "Cadastrar novo usuário",
            description = """
                Cadastra um novo usuário no sistema associado a um cliente existente.

                Regras de validação da senha:
                - Mínimo de 8 caracteres
                - Pelo menos uma letra maiúscula
                - Pelo menos uma letra minúscula
                - Pelo menos um número
                - Pelo menos um caractere especial (@$!%*?&)

                Após o cadastro, um email de boas-vindas é enviado automaticamente.
                """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "token_type": "Bearer",
                                      "expires_in": 86400,
                                      "user": {
                                        "id": 1,
                                        "email": "joao@email.com",
                                        "customerCode": "CUST001"
                                      }
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
                                            name = "Email já em uso",
                                            value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Email joao@email.com já está em uso"
                                            }
                                            """
                                    ),
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
                                            name = "Senhas não conferem",
                                            value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Senhas não conferem"
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    public Response register(@Valid RegisterRequest request) {
        LOG.infof("Recebida requisição de registro via API - Email: %s, Cliente: %s",
                  request.getEmail(), request.getCustomerCode());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            LOG.warn("Tentativa de registro com senhas não conferentes");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MessageResponse.of("Senhas não conferem"))
                    .build();
        }

        User user;

        // Se customerCode for fornecido, usa cliente existente
        if (request.hasCustomerCode()) {
            user = registerUserUseCase.execute(
                    request.getCustomerCode(),
                    request.getEmail(),
                    request.getPassword()
            );
        }
        // Senão, cria novo cliente com os dados fornecidos
        else if (request.hasCustomerData()) {
            user = registerUserUseCase.executeWithNewCustomer(
                    request.getFullName(),
                    request.getMotherName(),
                    request.getCpf(),
                    request.getRg(),
                    request.getZipCode(),
                    request.getStreet(),
                    request.getNumber(),
                    request.getComplement(),
                    request.getNeighborhood(),
                    request.getCity(),
                    request.getState(),
                    request.getBirthDate(),
                    request.getCellPhone(),
                    request.getEmail(),
                    request.getPassword()
            );
        } else {
            LOG.warn("Tentativa de registro sem customerCode nem dados do cliente");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MessageResponse.of(
                            "Forneça um customerCode de cliente existente OU os dados completos do novo cliente"
                    ))
                    .build();
        }

        String token = loginUseCase.execute(request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.fromToken(
                token,
                user.getId(),
                user.getEmail().getValue(),
                user.getCustomerCode(),
                jwtExpirationHours
        );

        LOG.infof("Registro via API concluído com sucesso - Email: %s", request.getEmail());

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(
            summary = "Fazer login",
            description = "Autentica um usuário e retorna um token JWT de acesso"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "token_type": "Bearer",
                                      "expires_in": 86400,
                                      "user": {
                                        "id": 1,
                                        "email": "joao@email.com",
                                        "customerCode": "CUST001"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "Credenciais inválidas"
                                    }
                                    """
                            )
                    )
            )
    })
    public Response login(@Valid LoginRequest request) {
        LOG.infof("Recebida requisição de login via API - Email: %s", request.getEmail());

        String token = loginUseCase.execute(request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationHours * 3600)
                .build();

        LOG.infof("Login via API concluído com sucesso - Email: %s", request.getEmail());

        return Response.ok(response).build();
    }

    @POST
    @Path("/forgot-password")
    @PermitAll
    @Operation(
            summary = "Esqueci minha senha",
            description = """
                Envia um email com um token para redefinição de senha.

                O token é válido por 1 hora.

                **Importante:** Por segurança, mesmo se o email não existir no sistema,
                a API retorna sucesso para não revelar quais emails estão cadastrados.
                """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Email enviado (se o email existir)",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "Se o email existir, um link de redefinição foi enviado"
                                    }
                                    """
                            )
                    )
            )
    })
    public Response forgotPassword(@Valid ForgotPasswordRequest request) {
        try {
            forgotPasswordUseCase.execute(request.getEmail());
        } catch (Exception e) {

        }

        return Response.ok(MessageResponse.of(
                "Se o email existir, um link de redefinição foi enviado"
        )).build();
    }

    @POST
    @Path("/reset-password")
    @PermitAll
    @Operation(
            summary = "Redefinir senha",
            description = """
                Redefine a senha do usuário usando o token recebido por email.

                O token é válido por 1 hora após a solicitação.
                """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Senha redefinida com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "Senha redefinida com sucesso"
                                    }
                                    """
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Token inválido ou expirado",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "Token inválido ou expirado"
                                    }
                                    """
                            )
                    )
            )
    })
    public Response resetPassword(@Valid ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MessageResponse.of("Senhas não conferem"))
                    .build();
        }

        resetPasswordUseCase.execute(request.getToken(), request.getNewPassword());

        return Response.ok(MessageResponse.of("Senha redefinida com sucesso")).build();
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"USER"})
    @Operation(
            summary = "Fazer logout",
            description = """
                Invalida o token JWT atual, impedindo seu uso futuro.

                O token deve ser enviado no header Authorization como "Bearer {token}".

                Após o logout, o token não poderá mais ser usado para acessar recursos protegidos.
                """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Logout realizado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "Logout realizado com sucesso"
                                    }
                                    """
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Token não fornecido ou inválido",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "Token de autenticação não fornecido"
                                    }
                                    """
                            )
                    )
            )
    })
    public Response logout(@Context HttpHeaders headers) {
        LOG.debug("Recebida requisição de logout via API");

        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || authHeader.isBlank()) {
            LOG.warn("Tentativa de logout sem token de autenticação");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(MessageResponse.of("Token de autenticação não fornecido"))
                    .build();
        }

        try {
            logoutUseCase.execute(authHeader);
            LOG.info("Logout via API realizado com sucesso");
            return Response.ok(MessageResponse.of("Logout realizado com sucesso")).build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Tentativa de logout com token inválido: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MessageResponse.of("Token inválido: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao processar logout via API");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(MessageResponse.of("Erro ao processar logout: " + e.getMessage()))
                    .build();
        }
    }
}
