import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Notifications e2e test', () => {
  const notificationsPageUrl = '/notifications';
  const notificationsPageUrlPattern = new RegExp('/notifications(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const notificationsSample = {};

  let notifications;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"box sympathetically","firstName":"Cecilia","lastName":"Muñiz Reyes"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/notifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/notifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/notifications/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (notifications) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notifications/${notifications.id}`,
      }).then(() => {
        notifications = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('Notifications menu should load Notifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('notifications');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Notifications').should('exist');
    cy.url().should('match', notificationsPageUrlPattern);
  });

  describe('Notifications page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notificationsPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Notifications page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/notifications/new$'));
        cy.getEntityCreateUpdateHeading('Notifications');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationsPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/notifications',
          body: {
            ...notificationsSample,
            user: user,
          },
        }).then(({ body }) => {
          notifications = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/notifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/notifications?page=0&size=20>; rel="last",<http://localhost/api/notifications?page=0&size=20>; rel="first"',
              },
              body: [notifications],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(notificationsPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(notificationsPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Notifications page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('notifications');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationsPageUrlPattern);
      });

      it('edit button click should load edit Notifications page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notifications');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationsPageUrlPattern);
      });

      it.skip('edit button click should load edit Notifications page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notifications');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationsPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Notifications', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('notifications').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationsPageUrlPattern);

        notifications = undefined;
      });
    });
  });

  describe('new Notifications page', () => {
    beforeEach(() => {
      cy.visit(`${notificationsPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Notifications');
    });

    it.skip('should create an instance of Notifications', () => {
      cy.get(`[data-cy="date"]`).type('2023-09-28T23:54');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2023-09-28T23:54');

      cy.get(`[data-cy="message"]`).type('quicker promptly awkwardly');
      cy.get(`[data-cy="message"]`).should('have.value', 'quicker promptly awkwardly');

      cy.get(`[data-cy="status"]`).select('PROMOCIONESPECIAL');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        notifications = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', notificationsPageUrlPattern);
    });
  });
});
