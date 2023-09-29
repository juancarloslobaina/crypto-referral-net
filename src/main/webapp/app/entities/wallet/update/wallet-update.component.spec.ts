import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICryptocurrency } from 'app/entities/cryptocurrency/cryptocurrency.model';
import { CryptocurrencyService } from 'app/entities/cryptocurrency/service/cryptocurrency.service';
import { IWallet } from '../wallet.model';
import { WalletService } from '../service/wallet.service';
import { WalletFormService } from './wallet-form.service';

import { WalletUpdateComponent } from './wallet-update.component';

describe('Wallet Management Update Component', () => {
  let comp: WalletUpdateComponent;
  let fixture: ComponentFixture<WalletUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let walletFormService: WalletFormService;
  let walletService: WalletService;
  let userService: UserService;
  let cryptocurrencyService: CryptocurrencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), WalletUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(WalletUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WalletUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    walletFormService = TestBed.inject(WalletFormService);
    walletService = TestBed.inject(WalletService);
    userService = TestBed.inject(UserService);
    cryptocurrencyService = TestBed.inject(CryptocurrencyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const wallet: IWallet = { id: 456 };
      const user: IUser = { id: 4037 };
      wallet.user = user;

      const userCollection: IUser[] = [{ id: 22809 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ wallet });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cryptocurrency query and add missing value', () => {
      const wallet: IWallet = { id: 456 };
      const cryto: ICryptocurrency = { id: 10947 };
      wallet.cryto = cryto;

      const cryptocurrencyCollection: ICryptocurrency[] = [{ id: 22899 }];
      jest.spyOn(cryptocurrencyService, 'query').mockReturnValue(of(new HttpResponse({ body: cryptocurrencyCollection })));
      const additionalCryptocurrencies = [cryto];
      const expectedCollection: ICryptocurrency[] = [...additionalCryptocurrencies, ...cryptocurrencyCollection];
      jest.spyOn(cryptocurrencyService, 'addCryptocurrencyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ wallet });
      comp.ngOnInit();

      expect(cryptocurrencyService.query).toHaveBeenCalled();
      expect(cryptocurrencyService.addCryptocurrencyToCollectionIfMissing).toHaveBeenCalledWith(
        cryptocurrencyCollection,
        ...additionalCryptocurrencies.map(expect.objectContaining),
      );
      expect(comp.cryptocurrenciesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const wallet: IWallet = { id: 456 };
      const user: IUser = { id: 13986 };
      wallet.user = user;
      const cryto: ICryptocurrency = { id: 16524 };
      wallet.cryto = cryto;

      activatedRoute.data = of({ wallet });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.cryptocurrenciesSharedCollection).toContain(cryto);
      expect(comp.wallet).toEqual(wallet);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWallet>>();
      const wallet = { id: 123 };
      jest.spyOn(walletFormService, 'getWallet').mockReturnValue(wallet);
      jest.spyOn(walletService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wallet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: wallet }));
      saveSubject.complete();

      // THEN
      expect(walletFormService.getWallet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(walletService.update).toHaveBeenCalledWith(expect.objectContaining(wallet));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWallet>>();
      const wallet = { id: 123 };
      jest.spyOn(walletFormService, 'getWallet').mockReturnValue({ id: null });
      jest.spyOn(walletService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wallet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: wallet }));
      saveSubject.complete();

      // THEN
      expect(walletFormService.getWallet).toHaveBeenCalled();
      expect(walletService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWallet>>();
      const wallet = { id: 123 };
      jest.spyOn(walletService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wallet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(walletService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCryptocurrency', () => {
      it('Should forward to cryptocurrencyService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(cryptocurrencyService, 'compareCryptocurrency');
        comp.compareCryptocurrency(entity, entity2);
        expect(cryptocurrencyService.compareCryptocurrency).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
