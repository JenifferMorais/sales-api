import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'paymentMethod',
  standalone: true
})
export class PaymentMethodPipe implements PipeTransform {
  private paymentMethods: { [key: string]: string } = {
    'DINHEIRO': 'Dinheiro',
    'CARTAO_CREDITO': 'Cartão de Crédito',
    'CARTAO_DEBITO': 'Cartão de Débito',
    'PIX': 'PIX',
    'TRANSFERENCIA_BANCARIA': 'Transferência Bancária',
    'Dinheiro': 'Dinheiro',
    'Cartão de Crédito': 'Cartão de Crédito',
    'Cartão de Débito': 'Cartão de Débito',
    'Transferência Bancária': 'Transferência Bancária'
  };

  transform(value: string | null | undefined): string {
    if (!value) {
      return '';
    }

    return this.paymentMethods[value] || value;
  }
}
