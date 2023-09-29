import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INotifications } from '../notifications.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../notifications.test-samples';

import { NotificationsService, RestNotifications } from './notifications.service';

const requireRestSample: RestNotifications = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('Notifications Service', () => {
  let service: NotificationsService;
  let httpMock: HttpTestingController;
  let expectedResult: INotifications | INotifications[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NotificationsService);
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

    it('should create a Notifications', () => {
      const notifications = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(notifications).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Notifications', () => {
      const notifications = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(notifications).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Notifications', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Notifications', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Notifications', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addNotificationsToCollectionIfMissing', () => {
      it('should add a Notifications to an empty array', () => {
        const notifications: INotifications = sampleWithRequiredData;
        expectedResult = service.addNotificationsToCollectionIfMissing([], notifications);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notifications);
      });

      it('should not add a Notifications to an array that contains it', () => {
        const notifications: INotifications = sampleWithRequiredData;
        const notificationsCollection: INotifications[] = [
          {
            ...notifications,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addNotificationsToCollectionIfMissing(notificationsCollection, notifications);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Notifications to an array that doesn't contain it", () => {
        const notifications: INotifications = sampleWithRequiredData;
        const notificationsCollection: INotifications[] = [sampleWithPartialData];
        expectedResult = service.addNotificationsToCollectionIfMissing(notificationsCollection, notifications);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notifications);
      });

      it('should add only unique Notifications to an array', () => {
        const notificationsArray: INotifications[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const notificationsCollection: INotifications[] = [sampleWithRequiredData];
        expectedResult = service.addNotificationsToCollectionIfMissing(notificationsCollection, ...notificationsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notifications: INotifications = sampleWithRequiredData;
        const notifications2: INotifications = sampleWithPartialData;
        expectedResult = service.addNotificationsToCollectionIfMissing([], notifications, notifications2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notifications);
        expect(expectedResult).toContain(notifications2);
      });

      it('should accept null and undefined values', () => {
        const notifications: INotifications = sampleWithRequiredData;
        expectedResult = service.addNotificationsToCollectionIfMissing([], null, notifications, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notifications);
      });

      it('should return initial array if no Notifications is added', () => {
        const notificationsCollection: INotifications[] = [sampleWithRequiredData];
        expectedResult = service.addNotificationsToCollectionIfMissing(notificationsCollection, undefined, null);
        expect(expectedResult).toEqual(notificationsCollection);
      });
    });

    describe('compareNotifications', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareNotifications(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareNotifications(entity1, entity2);
        const compareResult2 = service.compareNotifications(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareNotifications(entity1, entity2);
        const compareResult2 = service.compareNotifications(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareNotifications(entity1, entity2);
        const compareResult2 = service.compareNotifications(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
