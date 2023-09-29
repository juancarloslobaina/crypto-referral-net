import { IWallet, NewWallet } from './wallet.model';

export const sampleWithRequiredData: IWallet = {
  id: 29751,
};

export const sampleWithPartialData: IWallet = {
  id: 11709,
  amount: 3192.57,
};

export const sampleWithFullData: IWallet = {
  id: 9963,
  address: 'parallel',
  amount: 12107.2,
};

export const sampleWithNewData: NewWallet = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
