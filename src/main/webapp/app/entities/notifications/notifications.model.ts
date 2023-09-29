import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { NotificationStatus } from 'app/entities/enumerations/notification-status.model';

export interface INotifications {
  id: number;
  date?: dayjs.Dayjs | null;
  message?: string | null;
  status?: keyof typeof NotificationStatus | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewNotifications = Omit<INotifications, 'id'> & { id: null };
