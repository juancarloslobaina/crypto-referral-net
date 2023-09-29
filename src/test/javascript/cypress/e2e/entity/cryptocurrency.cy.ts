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

describe('Cryptocurrency e2e test', () => {
  const cryptocurrencyPageUrl = '/cryptocurrency';
  const cryptocurrencyPageUrlPattern = new RegExp('/cryptocurrency(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const cryptocurrencySample = {};

  let cryptocurrency;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/cryptocurrencies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cryptocurrencies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cryptocurrencies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (cryptocurrency) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cryptocurrencies/${cryptocurrency.id}`,
      }).then(() => {
        cryptocurrency = undefined;
      });
    }
  });

  it('Cryptocurrencies menu should load Cryptocurrencies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cryptocurrency');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Cryptocurrency').should('exist');
    cy.url().should('match', cryptocurrencyPageUrlPattern);
  });

  describe('Cryptocurrency page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cryptocurrencyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Cryptocurrency page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cryptocurrency/new$'));
        cy.getEntityCreateUpdateHeading('Cryptocurrency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', cryptocurrencyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cryptocurrencies',
          body: cryptocurrencySample,
        }).then(({ body }) => {
          cryptocurrency = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cryptocurrencies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cryptocurrencies?page=0&size=20>; rel="last",<http://localhost/api/cryptocurrencies?page=0&size=20>; rel="first"',
              },
              body: [cryptocurrency],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(cryptocurrencyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Cryptocurrency page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('cryptocurrency');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', cryptocurrencyPageUrlPattern);
      });

      it('edit button click should load edit Cryptocurrency page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Cryptocurrency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', cryptocurrencyPageUrlPattern);
      });

      it.skip('edit button click should load edit Cryptocurrency page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Cryptocurrency');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', cryptocurrencyPageUrlPattern);
      });

      it('last delete button click should delete instance of Cryptocurrency', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('cryptocurrency').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', cryptocurrencyPageUrlPattern);

        cryptocurrency = undefined;
      });
    });
  });

  describe('new Cryptocurrency page', () => {
    beforeEach(() => {
      cy.visit(`${cryptocurrencyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Cryptocurrency');
    });

    it('should create an instance of Cryptocurrency', () => {
      cy.get(`[data-cy="name"]`).type('even gosh mid');
      cy.get(`[data-cy="name"]`).should('have.value', 'even gosh mid');

      cy.get(`[data-cy="symbol"]`).type('abolish glass quixotic');
      cy.get(`[data-cy="symbol"]`).should('have.value', 'abolish glass quixotic');

      cy.get(`[data-cy="exchangeRate"]`).type('6666.39');
      cy.get(`[data-cy="exchangeRate"]`).should('have.value', '6666.39');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        cryptocurrency = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', cryptocurrencyPageUrlPattern);
    });
  });
});
