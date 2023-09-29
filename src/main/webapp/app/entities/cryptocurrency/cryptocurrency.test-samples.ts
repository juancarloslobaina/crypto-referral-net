import { ICryptocurrency, NewCryptocurrency } from './cryptocurrency.model';

export const sampleWithRequiredData: ICryptocurrency = {
  id: 28501,
};

export const sampleWithPartialData: ICryptocurrency = {
  id: 31444,
  name: 'unsteady obnoxiously following',
  symbol: 'med after slight',
  exchangeRate: 5651.83,
};

export const sampleWithFullData: ICryptocurrency = {
  id: 29458,
  name: 'medical',
  symbol: 'absolute wherever',
  exchangeRate: 12974.97,
};

export const sampleWithNewData: NewCryptocurrency = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
