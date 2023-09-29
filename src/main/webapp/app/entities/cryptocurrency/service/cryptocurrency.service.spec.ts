import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICryptocurrency } from '../cryptocurrency.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../cryptocurrency.test-samples';

import { CryptocurrencyService } from './cryptocurrency.service';

const requireRestSample: ICryptocurrency = {
  ...sampleWithRequiredData,
};

describe('Cryptocurrency Service', () => {
  let service: CryptocurrencyService;
  let httpMock: HttpTestingController;
  let expectedResult: ICryptocurrency | ICryptocurrency[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CryptocurrencyService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Cryptocurrency', () => {
      const cryptocurrency = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cryptocurrency).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cryptocurrency', () => {
      const cryptocurrency = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cryptocurrency).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cryptocurrency', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cryptocurrency', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Cryptocurrency', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCryptocurrencyToCollectionIfMissing', () => {
      it('should add a Cryptocurrency to an empty array', () => {
        const cryptocurrency: ICryptocurrency = sampleWithRequiredData;
        expectedResult = service.addCryptocurrencyToCollectionIfMissing([], cryptocurrency);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cryptocurrency);
      });

      it('should not add a Cryptocurrency to an array that contains it', () => {
        const cryptocurrency: ICryptocurrency = sampleWithRequiredData;
        const cryptocurrencyCollection: ICryptocurrency[] = [
          {
            ...cryptocurrency,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCryptocurrencyToCollectionIfMissing(cryptocurrencyCollection, cryptocurrency);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cryptocurrency to an array that doesn't contain it", () => {
        const cryptocurrency: ICryptocurrency = sampleWithRequiredData;
        const cryptocurrencyCollection: ICryptocurrency[] = [sampleWithPartialData];
        expectedResult = service.addCryptocurrencyToCollectionIfMissing(cryptocurrencyCollection, cryptocurrency);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cryptocurrency);
      });

      it('should add only unique Cryptocurrency to an array', () => {
        const cryptocurrencyArray: ICryptocurrency[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cryptocurrencyCollection: ICryptocurrency[] = [sampleWithRequiredData];
        expectedResult = service.addCryptocurrencyToCollectionIfMissing(cryptocurrencyCollection, ...cryptocurrencyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cryptocurrency: ICryptocurrency = sampleWithRequiredData;
        const cryptocurrency2: ICryptocurrency = sampleWithPartialData;
        expectedResult = service.addCryptocurrencyToCollectionIfMissing([], cryptocurrency, cryptocurrency2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cryptocurrency);
        expect(expectedResult).toContain(cryptocurrency2);
      });

      it('should accept null and undefined values', () => {
        const cryptocurrency: ICryptocurrency = sampleWithRequiredData;
        expectedResult = service.addCryptocurrencyToCollectionIfMissing([], null, cryptocurrency, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cryptocurrency);
      });

      it('should return initial array if no Cryptocurrency is added', () => {
        const cryptocurrencyCollection: ICryptocurrency[] = [sampleWithRequiredData];
        expectedResult = service.addCryptocurrencyToCollectionIfMissing(cryptocurrencyCollection, undefined, null);
        expect(expectedResult).toEqual(cryptocurrencyCollection);
      });
    });

    describe('compareCryptocurrency', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCryptocurrency(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCryptocurrency(entity1, entity2);
        const compareResult2 = service.compareCryptocurrency(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCryptocurrency(entity1, entity2);
        const compareResult2 = service.compareCryptocurrency(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCryptocurrency(entity1, entity2);
        const compareResult2 = service.compareCryptocurrency(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
