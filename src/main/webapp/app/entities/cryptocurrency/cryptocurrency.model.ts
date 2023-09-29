export interface ICryptocurrency {
  id: number;
  name?: string | null;
  symbol?: string | null;
  exchangeRate?: number | null;
}

export type NewCryptocurrency = Omit<ICryptocurrency, 'id'> & { id: null };
