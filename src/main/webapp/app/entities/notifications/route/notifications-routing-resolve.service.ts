import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INotifications } from '../notifications.model';
import { NotificationsService } from '../service/notifications.service';

export const notificationsResolve = (route: ActivatedRouteSnapshot): Observable<null | INotifications> => {
  const id = route.params['id'];
  if (id) {
    return inject(NotificationsService)
      .find(id)
      .pipe(
        mergeMap((notifications: HttpResponse<INotifications>) => {
          if (notifications.body) {
            return of(notifications.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default notificationsResolve;
