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

describe('Transaction e2e test', () => {
  const transactionPageUrl = '/transaction';
  const transactionPageUrlPattern = new RegExp('/transaction(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const transactionSample = {};

  let transaction;
  // let user;
  // let cryptocurrency;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"memorable localize","firstName":"Ramiro","lastName":"Monroy Carmona"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/cryptocurrencies',
      body: {"name":"freighter broil","symbol":"dimly availability astride","exchangeRate":28142.95},
    }).then(({ body }) => {
      cryptocurrency = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/transactions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/transactions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/transactions/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/cryptocurrencies', {
      statusCode: 200,
      body: [cryptocurrency],
    });

  });
   */

  afterEach(() => {
    if (transaction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/transactions/${transaction.id}`,
      }).then(() => {
        transaction = undefined;
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
    if (cryptocurrency) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cryptocurrencies/${cryptocurrency.id}`,
      }).then(() => {
        cryptocurrency = undefined;
      });
    }
  });
   */

  it('Transactions menu should load Transactions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Transaction').should('exist');
    cy.url().should('match', transactionPageUrlPattern);
  });

  describe('Transaction page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(transactionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Transaction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/transaction/new$'));
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/transactions',
          body: {
            ...transactionSample,
            userFrom: user,
            userTo: user,
            crypto: cryptocurrency,
          },
        }).then(({ body }) => {
          transaction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/transactions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/transactions?page=0&size=20>; rel="last",<http://localhost/api/transactions?page=0&size=20>; rel="first"',
              },
              body: [transaction],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(transactionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(transactionPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Transaction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('transaction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });

      it('edit button click should load edit Transaction page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });

      it.skip('edit button click should load edit Transaction page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Transaction', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('transaction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);

        transaction = undefined;
      });
    });
  });

  describe('new Transaction page', () => {
    beforeEach(() => {
      cy.visit(`${transactionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Transaction');
    });

    it.skip('should create an instance of Transaction', () => {
      cy.get(`[data-cy="amount"]`).type('20040.24');
      cy.get(`[data-cy="amount"]`).should('have.value', '20040.24');

      cy.get(`[data-cy="date"]`).type('2023-09-29T08:09');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2023-09-29T08:09');

      cy.get(`[data-cy="status"]`).select('COMPLETADA');

      cy.get(`[data-cy="userFrom"]`).select(1);
      cy.get(`[data-cy="userTo"]`).select(1);
      cy.get(`[data-cy="crypto"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        transaction = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', transactionPageUrlPattern);
    });
  });
});
