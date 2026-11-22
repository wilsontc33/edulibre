import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  templateUrl: './page-header.html'
})
export class PageHeader {
  readonly eyebrow = input<string>();
  readonly titulo = input.required<string>();
  readonly subtitulo = input<string>();
}
