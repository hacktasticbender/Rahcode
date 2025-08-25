'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import Layout from '@/components/layout/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { useApi } from '@/hooks/useApi';
import { CreateTicketRequest, Priority, Ticket } from '@/types';
import api from '@/lib/api';
import toast from 'react-hot-toast';

const CreateTicketPage: React.FC = () => {
  const router = useRouter();
  const { loading, execute } = useApi<Ticket>();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateTicketRequest>({
    defaultValues: {
      priority: Priority.MEDIUM,
    },
  });

  const onSubmit = async (data: CreateTicketRequest) => {
    setIsSubmitting(true);
    try {
      const ticket = await execute(
        () => api.post('/tickets', data),
        { showSuccessToast: true, successMessage: 'Ticket created successfully!' }
      );

      if (ticket) {
        router.push(`/tickets/${ticket.id}`);
      }
    } catch (error) {
      console.error('Failed to create ticket:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <ProtectedRoute>
      <Layout>
        <div className="max-w-2xl mx-auto">
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-gray-900">Create New Ticket</h1>
            <p className="text-gray-600">Submit a new support request</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-6">
            <div>
              <label htmlFor="subject" className="block text-sm font-medium text-gray-700 mb-2">
                Subject <span className="text-red-500">*</span>
              </label>
              <input
                {...register('subject', { required: 'Subject is required' })}
                type="text"
                className="input-field"
                placeholder="Brief description of the issue"
              />
              {errors.subject && (
                <p className="mt-1 text-sm text-red-600">{errors.subject.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                Description <span className="text-red-500">*</span>
              </label>
              <textarea
                {...register('description', { required: 'Description is required' })}
                rows={6}
                className="input-field"
                placeholder="Detailed description of the issue, including steps to reproduce, expected behavior, etc."
              />
              {errors.description && (
                <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="priority" className="block text-sm font-medium text-gray-700 mb-2">
                Priority
              </label>
              <select {...register('priority')} className="input-field">
                <option value={Priority.LOW}>Low - General questions or minor issues</option>
                <option value={Priority.MEDIUM}>Medium - Standard support requests</option>
                <option value={Priority.HIGH}>High - Issues affecting productivity</option>
                <option value={Priority.URGENT}>Urgent - Critical issues requiring immediate attention</option>
              </select>
            </div>

            <div className="flex justify-end space-x-4">
              <button
                type="button"
                onClick={() => router.back()}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isSubmitting || loading}
                className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isSubmitting ? 'Creating...' : 'Create Ticket'}
              </button>
            </div>
          </form>
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default CreateTicketPage;