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
import { ITransaction } from '../transaction.model';
import { TransactionService } from '../service/transaction.service';
import { TransactionFormService } from './transaction-form.service';

import { TransactionUpdateComponent } from './transaction-update.component';

describe('Transaction Management Update Component', () => {
  let comp: TransactionUpdateComponent;
  let fixture: ComponentFixture<TransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transactionFormService: TransactionFormService;
  let transactionService: TransactionService;
  let userService: UserService;
  let cryptocurrencyService: CryptocurrencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TransactionUpdateComponent],
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
      .overrideTemplate(TransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transactionFormService = TestBed.inject(TransactionFormService);
    transactionService = TestBed.inject(TransactionService);
    userService = TestBed.inject(UserService);
    cryptocurrencyService = TestBed.inject(CryptocurrencyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const transaction: ITransaction = { id: 456 };
      const userFrom: IUser = { id: 25470 };
      transaction.userFrom = userFrom;
      const userTo: IUser = { id: 23851 };
      transaction.userTo = userTo;

      const userCollection: IUser[] = [{ id: 32305 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [userFrom, userTo];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cryptocurrency query and add missing value', () => {
      const transaction: ITransaction = { id: 456 };
      const crypto: ICryptocurrency = { id: 23714 };
      transaction.crypto = crypto;

      const cryptocurrencyCollection: ICryptocurrency[] = [{ id: 22986 }];
      jest.spyOn(cryptocurrencyService, 'query').mockReturnValue(of(new HttpResponse({ body: cryptocurrencyCollection })));
      const additionalCryptocurrencies = [crypto];
      const expectedCollection: ICryptocurrency[] = [...additionalCryptocurrencies, ...cryptocurrencyCollection];
      jest.spyOn(cryptocurrencyService, 'addCryptocurrencyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(cryptocurrencyService.query).toHaveBeenCalled();
      expect(cryptocurrencyService.addCryptocurrencyToCollectionIfMissing).toHaveBeenCalledWith(
        cryptocurrencyCollection,
        ...additionalCryptocurrencies.map(expect.objectContaining),
      );
      expect(comp.cryptocurrenciesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const transaction: ITransaction = { id: 456 };
      const userFrom: IUser = { id: 2056 };
      transaction.userFrom = userFrom;
      const userTo: IUser = { id: 24014 };
      transaction.userTo = userTo;
      const crypto: ICryptocurrency = { id: 2554 };
      transaction.crypto = crypto;

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(userFrom);
      expect(comp.usersSharedCollection).toContain(userTo);
      expect(comp.cryptocurrenciesSharedCollection).toContain(crypto);
      expect(comp.transaction).toEqual(transaction);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionFormService, 'getTransaction').mockReturnValue(transaction);
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionFormService.getTransaction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(transactionService.update).toHaveBeenCalledWith(expect.objectContaining(transaction));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionFormService, 'getTransaction').mockReturnValue({ id: null });
      jest.spyOn(transactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionFormService.getTransaction).toHaveBeenCalled();
      expect(transactionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transactionService.update).toHaveBeenCalled();
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
