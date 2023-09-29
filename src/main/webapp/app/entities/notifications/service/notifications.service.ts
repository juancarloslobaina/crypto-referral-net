import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INotifications, NewNotifications } from '../notifications.model';

export type PartialUpdateNotifications = Partial<INotifications> & Pick<INotifications, 'id'>;

type RestOf<T extends INotifications | NewNotifications> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestNotifications = RestOf<INotifications>;

export type NewRestNotifications = RestOf<NewNotifications>;

export type PartialUpdateRestNotifications = RestOf<PartialUpdateNotifications>;

export type EntityResponseType = HttpResponse<INotifications>;
export type EntityArrayResponseType = HttpResponse<INotifications[]>;

@Injectable({ providedIn: 'root' })
export class NotificationsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notifications');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(notifications: NewNotifications): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notifications);
    return this.http
      .post<RestNotifications>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(notifications: INotifications): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notifications);
    return this.http
      .put<RestNotifications>(`${this.resourceUrl}/${this.getNotificationsIdentifier(notifications)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(notifications: PartialUpdateNotifications): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notifications);
    return this.http
      .patch<RestNotifications>(`${this.resourceUrl}/${this.getNotificationsIdentifier(notifications)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestNotifications>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestNotifications[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getNotificationsIdentifier(notifications: Pick<INotifications, 'id'>): number {
    return notifications.id;
  }

  compareNotifications(o1: Pick<INotifications, 'id'> | null, o2: Pick<INotifications, 'id'> | null): boolean {
    return o1 && o2 ? this.getNotificationsIdentifier(o1) === this.getNotificationsIdentifier(o2) : o1 === o2;
  }

  addNotificationsToCollectionIfMissing<Type extends Pick<INotifications, 'id'>>(
    notificationsCollection: Type[],
    ...notificationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const notifications: Type[] = notificationsToCheck.filter(isPresent);
    if (notifications.length > 0) {
      const notificationsCollectionIdentifiers = notificationsCollection.map(
        notificationsItem => this.getNotificationsIdentifier(notificationsItem)!,
      );
      const notificationsToAdd = notifications.filter(notificationsItem => {
        const notificationsIdentifier = this.getNotificationsIdentifier(notificationsItem);
        if (notificationsCollectionIdentifiers.includes(notificationsIdentifier)) {
          return false;
        }
        notificationsCollectionIdentifiers.push(notificationsIdentifier);
        return true;
      });
      return [...notificationsToAdd, ...notificationsCollection];
    }
    return notificationsCollection;
  }

  protected convertDateFromClient<T extends INotifications | NewNotifications | PartialUpdateNotifications>(notifications: T): RestOf<T> {
    return {
      ...notifications,
      date: notifications.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restNotifications: RestNotifications): INotifications {
    return {
      ...restNotifications,
      date: restNotifications.date ? dayjs(restNotifications.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestNotifications>): HttpResponse<INotifications> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestNotifications[]>): HttpResponse<INotifications[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
