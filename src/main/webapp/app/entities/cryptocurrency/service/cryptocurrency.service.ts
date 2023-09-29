import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICryptocurrency, NewCryptocurrency } from '../cryptocurrency.model';

export type PartialUpdateCryptocurrency = Partial<ICryptocurrency> & Pick<ICryptocurrency, 'id'>;

export type EntityResponseType = HttpResponse<ICryptocurrency>;
export type EntityArrayResponseType = HttpResponse<ICryptocurrency[]>;

@Injectable({ providedIn: 'root' })
export class CryptocurrencyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cryptocurrencies');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(cryptocurrency: NewCryptocurrency): Observable<EntityResponseType> {
    return this.http.post<ICryptocurrency>(this.resourceUrl, cryptocurrency, { observe: 'response' });
  }

  update(cryptocurrency: ICryptocurrency): Observable<EntityResponseType> {
    return this.http.put<ICryptocurrency>(`${this.resourceUrl}/${this.getCryptocurrencyIdentifier(cryptocurrency)}`, cryptocurrency, {
      observe: 'response',
    });
  }

  partialUpdate(cryptocurrency: PartialUpdateCryptocurrency): Observable<EntityResponseType> {
    return this.http.patch<ICryptocurrency>(`${this.resourceUrl}/${this.getCryptocurrencyIdentifier(cryptocurrency)}`, cryptocurrency, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICryptocurrency>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICryptocurrency[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCryptocurrencyIdentifier(cryptocurrency: Pick<ICryptocurrency, 'id'>): number {
    return cryptocurrency.id;
  }

  compareCryptocurrency(o1: Pick<ICryptocurrency, 'id'> | null, o2: Pick<ICryptocurrency, 'id'> | null): boolean {
    return o1 && o2 ? this.getCryptocurrencyIdentifier(o1) === this.getCryptocurrencyIdentifier(o2) : o1 === o2;
  }

  addCryptocurrencyToCollectionIfMissing<Type extends Pick<ICryptocurrency, 'id'>>(
    cryptocurrencyCollection: Type[],
    ...cryptocurrenciesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cryptocurrencies: Type[] = cryptocurrenciesToCheck.filter(isPresent);
    if (cryptocurrencies.length > 0) {
      const cryptocurrencyCollectionIdentifiers = cryptocurrencyCollection.map(
        cryptocurrencyItem => this.getCryptocurrencyIdentifier(cryptocurrencyItem)!,
      );
      const cryptocurrenciesToAdd = cryptocurrencies.filter(cryptocurrencyItem => {
        const cryptocurrencyIdentifier = this.getCryptocurrencyIdentifier(cryptocurrencyItem);
        if (cryptocurrencyCollectionIdentifiers.includes(cryptocurrencyIdentifier)) {
          return false;
        }
        cryptocurrencyCollectionIdentifiers.push(cryptocurrencyIdentifier);
        return true;
      });
      return [...cryptocurrenciesToAdd, ...cryptocurrencyCollection];
    }
    return cryptocurrencyCollection;
  }
}
