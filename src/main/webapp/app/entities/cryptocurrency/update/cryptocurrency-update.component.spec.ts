import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CryptocurrencyService } from '../service/cryptocurrency.service';
import { ICryptocurrency } from '../cryptocurrency.model';
import { CryptocurrencyFormService } from './cryptocurrency-form.service';

import { CryptocurrencyUpdateComponent } from './cryptocurrency-update.component';

describe('Cryptocurrency Management Update Component', () => {
  let comp: CryptocurrencyUpdateComponent;
  let fixture: ComponentFixture<CryptocurrencyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cryptocurrencyFormService: CryptocurrencyFormService;
  let cryptocurrencyService: CryptocurrencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), CryptocurrencyUpdateComponent],
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
      .overrideTemplate(CryptocurrencyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CryptocurrencyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cryptocurrencyFormService = TestBed.inject(CryptocurrencyFormService);
    cryptocurrencyService = TestBed.inject(CryptocurrencyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const cryptocurrency: ICryptocurrency = { id: 456 };

      activatedRoute.data = of({ cryptocurrency });
      comp.ngOnInit();

      expect(comp.cryptocurrency).toEqual(cryptocurrency);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICryptocurrency>>();
      const cryptocurrency = { id: 123 };
      jest.spyOn(cryptocurrencyFormService, 'getCryptocurrency').mockReturnValue(cryptocurrency);
      jest.spyOn(cryptocurrencyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cryptocurrency });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cryptocurrency }));
      saveSubject.complete();

      // THEN
      expect(cryptocurrencyFormService.getCryptocurrency).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cryptocurrencyService.update).toHaveBeenCalledWith(expect.objectContaining(cryptocurrency));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICryptocurrency>>();
      const cryptocurrency = { id: 123 };
      jest.spyOn(cryptocurrencyFormService, 'getCryptocurrency').mockReturnValue({ id: null });
      jest.spyOn(cryptocurrencyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cryptocurrency: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cryptocurrency }));
      saveSubject.complete();

      // THEN
      expect(cryptocurrencyFormService.getCryptocurrency).toHaveBeenCalled();
      expect(cryptocurrencyService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICryptocurrency>>();
      const cryptocurrency = { id: 123 };
      jest.spyOn(cryptocurrencyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cryptocurrency });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cryptocurrencyService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
