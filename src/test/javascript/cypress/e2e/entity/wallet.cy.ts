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

describe('Wallet e2e test', () => {
  const walletPageUrl = '/wallet';
  const walletPageUrlPattern = new RegExp('/wallet(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const walletSample = {};

  let wallet;
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
      body: {"login":"onto as unlock","firstName":"Jacobo","lastName":"Ruíz Lozada"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/cryptocurrencies',
      body: {"name":"distend","symbol":"now frank","exchangeRate":12472.95},
    }).then(({ body }) => {
      cryptocurrency = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/wallets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/wallets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/wallets/*').as('deleteEntityRequest');
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
    if (wallet) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/wallets/${wallet.id}`,
      }).then(() => {
        wallet = undefined;
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

  it('Wallets menu should load Wallets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Wallet').should('exist');
    cy.url().should('match', walletPageUrlPattern);
  });

  describe('Wallet page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(walletPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Wallet page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/wallet/new$'));
        cy.getEntityCreateUpdateHeading('Wallet');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', walletPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/wallets',
          body: {
            ...walletSample,
            user: user,
            cryto: cryptocurrency,
          },
        }).then(({ body }) => {
          wallet = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/wallets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/wallets?page=0&size=20>; rel="last",<http://localhost/api/wallets?page=0&size=20>; rel="first"',
              },
              body: [wallet],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(walletPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(walletPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Wallet page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('wallet');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', walletPageUrlPattern);
      });

      it('edit button click should load edit Wallet page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Wallet');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', walletPageUrlPattern);
      });

      it.skip('edit button click should load edit Wallet page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Wallet');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', walletPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Wallet', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('wallet').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', walletPageUrlPattern);

        wallet = undefined;
      });
    });
  });

  describe('new Wallet page', () => {
    beforeEach(() => {
      cy.visit(`${walletPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Wallet');
    });

    it.skip('should create an instance of Wallet', () => {
      cy.get(`[data-cy="address"]`).type('yet pish hobby');
      cy.get(`[data-cy="address"]`).should('have.value', 'yet pish hobby');

      cy.get(`[data-cy="amount"]`).type('11004.24');
      cy.get(`[data-cy="amount"]`).should('have.value', '11004.24');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="cryto"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        wallet = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', walletPageUrlPattern);
    });
  });
});
