import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { NotificationsComponent } from './list/notifications.component';
import { NotificationsDetailComponent } from './detail/notifications-detail.component';
import { NotificationsUpdateComponent } from './update/notifications-update.component';
import NotificationsResolve from './route/notifications-routing-resolve.service';

const notificationsRoute: Routes = [
  {
    path: '',
    component: NotificationsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NotificationsDetailComponent,
    resolve: {
      notifications: NotificationsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotificationsUpdateComponent,
    resolve: {
      notifications: NotificationsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NotificationsUpdateComponent,
    resolve: {
      notifications: NotificationsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default notificationsRoute;
