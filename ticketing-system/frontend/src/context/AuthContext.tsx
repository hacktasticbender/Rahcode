'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { User, LoginRequest, RegisterRequest, JwtResponse, ApiResponse } from '@/types';
import { getStoredUser, getStoredToken, setAuthData, clearAuthData } from '@/lib/auth';
import api from '@/lib/api';
import toast from 'react-hot-toast';

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (credentials: LoginRequest) => Promise<boolean>;
  register: (userData: RegisterRequest) => Promise<boolean>;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = () => {
      const storedUser = getStoredUser();
      const storedToken = getStoredToken();
      
      setUser(storedUser);
      setToken(storedToken);
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      const response = await api.post<ApiResponse<JwtResponse>>('/auth/login', credentials);
      
      if (response.data.success) {
        const authData = response.data.data;
        setAuthData(authData);
        setUser(getStoredUser());
        setToken(authData.token);
        toast.success('Login successful!');
        return true;
      } else {
        toast.error(response.data.message || 'Login failed');
        return false;
      }
    } catch (error: any) {
      const message = error.response?.data?.message || 'Login failed';
      toast.error(message);
      return false;
    }
  };

  const register = async (userData: RegisterRequest): Promise<boolean> => {
    try {
      const response = await api.post<ApiResponse<User>>('/auth/register', userData);
      
      if (response.data.success) {
        toast.success('Registration successful! Please login.');
        return true;
      } else {
        toast.error(response.data.message || 'Registration failed');
        return false;
      }
    } catch (error: any) {
      const message = error.response?.data?.message || 'Registration failed';
      toast.error(message);
      return false;
    }
  };

  const logout = () => {
    clearAuthData();
    setUser(null);
    setToken(null);
    toast.success('Logged out successfully');
  };

  const refreshUser = async () => {
    try {
      const response = await api.get<ApiResponse<User>>('/users/me');
      if (response.data.success) {
        const updatedUser = response.data.data;
        setUser(updatedUser);
        // Update stored user data
        const storedToken = getStoredToken();
        if (storedToken) {
          setAuthData({
            token: storedToken,
            type: 'Bearer',
            id: updatedUser.id,
            username: updatedUser.username,
            email: updatedUser.email,
            role: updatedUser.role,
          });
        }
      }
    } catch (error) {
      console.error('Failed to refresh user data:', error);
    }
  };

  const value = {
    user,
    token,
    loading,
    login,
    register,
    logout,
    refreshUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};