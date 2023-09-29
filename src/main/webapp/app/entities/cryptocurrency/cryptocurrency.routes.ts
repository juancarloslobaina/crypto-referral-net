import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CryptocurrencyComponent } from './list/cryptocurrency.component';
import { CryptocurrencyDetailComponent } from './detail/cryptocurrency-detail.component';
import { CryptocurrencyUpdateComponent } from './update/cryptocurrency-update.component';
import CryptocurrencyResolve from './route/cryptocurrency-routing-resolve.service';

const cryptocurrencyRoute: Routes = [
  {
    path: '',
    component: CryptocurrencyComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CryptocurrencyDetailComponent,
    resolve: {
      cryptocurrency: CryptocurrencyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CryptocurrencyUpdateComponent,
    resolve: {
      cryptocurrency: CryptocurrencyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CryptocurrencyUpdateComponent,
    resolve: {
      cryptocurrency: CryptocurrencyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cryptocurrencyRoute;
