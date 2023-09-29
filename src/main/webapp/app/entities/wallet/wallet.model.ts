import { IUser } from 'app/entities/user/user.model';
import { ICryptocurrency } from 'app/entities/cryptocurrency/cryptocurrency.model';

export interface IWallet {
  id: number;
  address?: string | null;
  balance?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  cryto?: Pick<ICryptocurrency, 'id' | 'symbol'> | null;
}

export type NewWallet = Omit<IWallet, 'id'> & { id: null };
