import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AlertService } from '../../shared/services/alert/alert.service';

function extractErrorMessage(error: HttpErrorResponse): string {
  if (error.error) {
    if (error.error.message) {
      return formatBackendMessage(error.error.message);
    }

    if (typeof error.error === 'string') {
      return formatBackendMessage(error.error);
    }

    if (Array.isArray(error.error.errors) && error.error.errors.length > 0) {
      return error.error.errors[0].message || error.error.errors[0];
    }
  }

  return '';
}

function formatBackendMessage(message: string): string {
  if (message.includes('No enum constant')) {
    const match = message.match(/No enum constant .*\.(\w+)$/);
    if (match) {
      return `Valor inválido: "${match[1]}". Por favor, selecione uma opção válida.`;
    }
    return 'Valor selecionado é inválido. Por favor, selecione uma opção válida.';
  }

  if (message.includes('ConstraintViolation') || message.includes('constraint')) {
    return 'Dados inválidos. Verifique os campos e tente novamente.';
  }

  if (message.includes('duplicate') || message.includes('já existe')) {
    return 'Este registro já existe no sistema.';
  }

  return message;
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const alerts = inject(AlertService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Ocorreu um erro inesperado';

      if (error.error instanceof ErrorEvent) {
        errorMessage = `Erro: ${error.error.message}`;
      } else {
        switch (error.status) {
          case 0:
            errorMessage = 'Não foi possível conectar ao servidor';
            break;
          case 401:
            errorMessage = extractErrorMessage(error) || 'Não autorizado. Faça login novamente.';
            localStorage.clear();
            router.navigate(['/auth/login']);
            break;
          case 403:
            errorMessage = extractErrorMessage(error) || 'Você não tem permissão para acessar este recurso';
            break;
          case 404:
            errorMessage = extractErrorMessage(error) || 'Recurso não encontrado';
            break;
          case 409:
            errorMessage = extractErrorMessage(error) || 'Conflito ao processar a solicitação';
            break;
          case 422:
            errorMessage = extractErrorMessage(error) || 'Dados inválidos';
            break;
          case 500:
            const backendMsg = extractErrorMessage(error);
            errorMessage = backendMsg || 'Erro interno do servidor';
            break;
          default:
            const defaultMsg = extractErrorMessage(error);
            errorMessage = defaultMsg || `Erro ${error.status}: ${error.statusText}`;
        }
      }

      (error as any).userMessage = errorMessage;

      return throwError(() => error);
    })
  );
};
