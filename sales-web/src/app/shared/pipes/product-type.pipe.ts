import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'productType',
  standalone: true
})
export class ProductTypePipe implements PipeTransform {
  private productTypes: { [key: string]: string } = {
    'LIPS': 'Lábios',
    'FACE': 'Rosto',
    'EYES': 'Olhos',
    'NAILS': 'Unhas',
    'SKIN_CARE': 'Cuidados com a Pele',
    'HAIR': 'Cabelos',
    'FRAGRANCE': 'Fragrância',
    'OTHER': 'Outro',
    'Lábios': 'Lábios',
    'Rosto': 'Rosto',
    'Olhos': 'Olhos',
    'Unhas': 'Unhas',
    'Cuidados com a Pele': 'Cuidados com a Pele',
    'Cabelos': 'Cabelos',
    'Fragrância': 'Fragrância',
    'Outro': 'Outro'
  };

  transform(value: string | null | undefined): string {
    if (!value) {
      return '';
    }

    return this.productTypes[value] || value;
  }
}
