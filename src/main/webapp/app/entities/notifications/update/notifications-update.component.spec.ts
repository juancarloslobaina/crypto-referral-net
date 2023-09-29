import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { NotificationsService } from '../service/notifications.service';
import { INotifications } from '../notifications.model';

import { NotificationsFormService } from './notifications-form.service';

import { NotificationsUpdateComponent } from './notifications-update.component';

describe('Notifications Management Update Component', () => {
  let comp: NotificationsUpdateComponent;
  let fixture: ComponentFixture<NotificationsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationsFormService: NotificationsFormService;
  let notificationsService: NotificationsService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), NotificationsUpdateComponent],
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
      .overrideTemplate(NotificationsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationsFormService = TestBed.inject(NotificationsFormService);
    notificationsService = TestBed.inject(NotificationsService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const notifications: INotifications = { id: 456 };
      const user: IUser = { id: 23016 };
      notifications.user = user;

      const userCollection: IUser[] = [{ id: 11939 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notifications });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notifications: INotifications = { id: 456 };
      const user: IUser = { id: 32750 };
      notifications.user = user;

      activatedRoute.data = of({ notifications });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.notifications).toEqual(notifications);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotifications>>();
      const notifications = { id: 123 };
      jest.spyOn(notificationsFormService, 'getNotifications').mockReturnValue(notifications);
      jest.spyOn(notificationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notifications });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notifications }));
      saveSubject.complete();

      // THEN
      expect(notificationsFormService.getNotifications).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationsService.update).toHaveBeenCalledWith(expect.objectContaining(notifications));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotifications>>();
      const notifications = { id: 123 };
      jest.spyOn(notificationsFormService, 'getNotifications').mockReturnValue({ id: null });
      jest.spyOn(notificationsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notifications: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notifications }));
      saveSubject.complete();

      // THEN
      expect(notificationsFormService.getNotifications).toHaveBeenCalled();
      expect(notificationsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotifications>>();
      const notifications = { id: 123 };
      jest.spyOn(notificationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notifications });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationsService.update).toHaveBeenCalled();
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
  });
});
