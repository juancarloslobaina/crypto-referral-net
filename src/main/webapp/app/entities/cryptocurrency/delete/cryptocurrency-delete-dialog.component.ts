import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICryptocurrency } from '../cryptocurrency.model';
import { CryptocurrencyService } from '../service/cryptocurrency.service';

@Component({
  standalone: true,
  templateUrl: './cryptocurrency-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CryptocurrencyDeleteDialogComponent {
  cryptocurrency?: ICryptocurrency;

  constructor(
    protected cryptocurrencyService: CryptocurrencyService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cryptocurrencyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
