import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { NotificationStatus } from 'app/entities/enumerations/notification-status.model';
import { NotificationsService } from '../service/notifications.service';
import { INotifications } from '../notifications.model';
import { NotificationsFormService, NotificationsFormGroup } from './notifications-form.service';

@Component({
  standalone: true,
  selector: 'jhi-notifications-update',
  templateUrl: './notifications-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class NotificationsUpdateComponent implements OnInit {
  isSaving = false;
  notifications: INotifications | null = null;
  notificationStatusValues = Object.keys(NotificationStatus);

  usersSharedCollection: IUser[] = [];

  editForm: NotificationsFormGroup = this.notificationsFormService.createNotificationsFormGroup();

  constructor(
    protected notificationsService: NotificationsService,
    protected notificationsFormService: NotificationsFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notifications }) => {
      this.notifications = notifications;
      if (notifications) {
        this.updateForm(notifications);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notifications = this.notificationsFormService.getNotifications(this.editForm);
    if (notifications.id !== null) {
      this.subscribeToSaveResponse(this.notificationsService.update(notifications));
    } else {
      this.subscribeToSaveResponse(this.notificationsService.create(notifications));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotifications>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(notifications: INotifications): void {
    this.notifications = notifications;
    this.notificationsFormService.resetForm(this.editForm, notifications);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, notifications.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.notifications?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
