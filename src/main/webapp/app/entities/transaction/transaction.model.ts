import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICryptocurrency } from 'app/entities/cryptocurrency/cryptocurrency.model';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';

export interface ITransaction {
  id: number;
  amount?: number | null;
  date?: dayjs.Dayjs | null;
  status?: keyof typeof TransactionStatus | null;
  userFrom?: Pick<IUser, 'id' | 'login'> | null;
  userTo?: Pick<IUser, 'id' | 'login'> | null;
  crypto?: Pick<ICryptocurrency, 'id' | 'symbol'> | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
