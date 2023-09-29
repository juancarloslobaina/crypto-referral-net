import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICryptocurrency } from '../cryptocurrency.model';
import { CryptocurrencyService } from '../service/cryptocurrency.service';

export const cryptocurrencyResolve = (route: ActivatedRouteSnapshot): Observable<null | ICryptocurrency> => {
  const id = route.params['id'];
  if (id) {
    return inject(CryptocurrencyService)
      .find(id)
      .pipe(
        mergeMap((cryptocurrency: HttpResponse<ICryptocurrency>) => {
          if (cryptocurrency.body) {
            return of(cryptocurrency.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default cryptocurrencyResolve;
