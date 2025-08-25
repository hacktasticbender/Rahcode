import Cookies from 'js-cookie';
import { User, JwtResponse } from '@/types';

export const getStoredUser = (): User | null => {
  if (typeof window === 'undefined') return null;
  
  try {
    const userStr = Cookies.get('user');
    return userStr ? JSON.parse(userStr) : null;
  } catch {
    return null;
  }
};

export const getStoredToken = (): string | null => {
  if (typeof window === 'undefined') return null;
  return Cookies.get('token') || null;
};

export const setAuthData = (authData: JwtResponse) => {
  const user: User = {
    id: authData.id,
    username: authData.username,
    email: authData.email,
    firstName: '',
    lastName: '',
    role: authData.role as any,
    enabled: true,
    createdAt: '',
    updatedAt: '',
  };

  Cookies.set('token', authData.token, { expires: 1 }); // 1 day
  Cookies.set('user', JSON.stringify(user), { expires: 1 });
};

export const clearAuthData = () => {
  Cookies.remove('token');
  Cookies.remove('user');
};

export const isAuthenticated = (): boolean => {
  return !!getStoredToken();
};

export const hasRole = (requiredRole: string): boolean => {
  const user = getStoredUser();
  if (!user) return false;
  
  // Role hierarchy: ADMIN > SUPPORT_AGENT > USER
  const roleHierarchy = ['USER', 'SUPPORT_AGENT', 'ADMIN'];
  const userRoleIndex = roleHierarchy.indexOf(user.role);
  const requiredRoleIndex = roleHierarchy.indexOf(requiredRole);
  
  return userRoleIndex >= requiredRoleIndex;
};