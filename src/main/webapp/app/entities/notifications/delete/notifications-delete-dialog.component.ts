import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { INotifications } from '../notifications.model';
import { NotificationsService } from '../service/notifications.service';

@Component({
  standalone: true,
  templateUrl: './notifications-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class NotificationsDeleteDialogComponent {
  notifications?: INotifications;

  constructor(
    protected notificationsService: NotificationsService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.notificationsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
