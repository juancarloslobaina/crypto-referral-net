import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'cryptocurrency',
        data: { pageTitle: 'cryptoApp.cryptocurrency.home.title' },
        loadChildren: () => import('./cryptocurrency/cryptocurrency.routes'),
      },
      {
        path: 'notifications',
        data: { pageTitle: 'cryptoApp.notifications.home.title' },
        loadChildren: () => import('./notifications/notifications.routes'),
      },
      {
        path: 'transaction',
        data: { pageTitle: 'cryptoApp.transaction.home.title' },
        loadChildren: () => import('./transaction/transaction.routes'),
      },
      {
        path: 'wallet',
        data: { pageTitle: 'cryptoApp.wallet.home.title' },
        loadChildren: () => import('./wallet/wallet.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
