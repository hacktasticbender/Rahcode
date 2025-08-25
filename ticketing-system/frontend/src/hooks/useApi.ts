import { useState, useCallback } from 'react';
import { ApiResponse } from '@/types';
import api from '@/lib/api';
import toast from 'react-hot-toast';

interface UseApiOptions {
  showSuccessToast?: boolean;
  showErrorToast?: boolean;
  successMessage?: string;
}

export const useApi = <T = any>() => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<T | null>(null);

  const execute = useCallback(async (
    apiCall: () => Promise<{ data: ApiResponse<T> }>,
    options: UseApiOptions = {}
  ) => {
    const {
      showSuccessToast = false,
      showErrorToast = true,
      successMessage = 'Operation completed successfully'
    } = options;

    try {
      setLoading(true);
      setError(null);
      
      const response = await apiCall();
      
      if (response.data.success) {
        setData(response.data.data);
        if (showSuccessToast) {
          toast.success(response.data.message || successMessage);
        }
        return response.data.data;
      } else {
        const errorMessage = response.data.message || 'Operation failed';
        setError(errorMessage);
        if (showErrorToast) {
          toast.error(errorMessage);
        }
        throw new Error(errorMessage);
      }
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'An error occurred';
      setError(errorMessage);
      if (showErrorToast) {
        toast.error(errorMessage);
      }
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setLoading(false);
    setError(null);
    setData(null);
  }, []);

  return {
    loading,
    error,
    data,
    execute,
    reset
  };
};