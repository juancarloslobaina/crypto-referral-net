import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ICryptocurrency } from '../cryptocurrency.model';

@Component({
  standalone: true,
  selector: 'jhi-cryptocurrency-detail',
  templateUrl: './cryptocurrency-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class CryptocurrencyDetailComponent {
  @Input() cryptocurrency: ICryptocurrency | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
