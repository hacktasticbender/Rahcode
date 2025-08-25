'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import Layout from '@/components/layout/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { useApi } from '@/hooks/useApi';
import { Ticket, PageResponse, Status, Priority } from '@/types';
import api from '@/lib/api';
import {
  TicketIcon,
  PlusIcon,
  MagnifyingGlassIcon,
} from '@heroicons/react/24/outline';

const TicketsPage: React.FC = () => {
  const { loading, execute } = useApi<PageResponse<Ticket>>();
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<Status | ''>('');
  const [priorityFilter, setPriorityFilter] = useState<Priority | ''>('');

  useEffect(() => {
    loadTickets();
  }, [searchTerm, statusFilter, priorityFilter]);

  const loadTickets = async () => {
    try {
      const params = new URLSearchParams();
      if (searchTerm) params.append('search', searchTerm);
      if (statusFilter) params.append('status', statusFilter);
      if (priorityFilter) params.append('priority', priorityFilter);
      params.append('page', '0');
      params.append('size', '20');
      params.append('sortBy', 'createdAt');
      params.append('sortDir', 'desc');

      const response = await execute(
        () => api.get(`/tickets/my?${params.toString()}`)
      );

      if (response) {
        setTickets(response.content);
      }
    } catch (error) {
      console.error('Failed to load tickets:', error);
    }
  };

  const getStatusColor = (status: Status) => {
    switch (status) {
      case Status.OPEN:
        return 'bg-red-100 text-red-800';
      case Status.IN_PROGRESS:
        return 'bg-yellow-100 text-yellow-800';
      case Status.RESOLVED:
        return 'bg-green-100 text-green-800';
      case Status.CLOSED:
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getPriorityColor = (priority: Priority) => {
    switch (priority) {
      case Priority.URGENT:
        return 'text-red-600';
      case Priority.HIGH:
        return 'text-orange-600';
      case Priority.MEDIUM:
        return 'text-yellow-600';
      case Priority.LOW:
        return 'text-green-600';
      default:
        return 'text-gray-600';
    }
  };

  return (
    <ProtectedRoute>
      <Layout>
        <div className="space-y-6">
          {/* Header */}
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold text-gray-900">My Tickets</h1>
            <Link href="/tickets/create" className="btn-primary flex items-center">
              <PlusIcon className="h-5 w-5 mr-2" />
              Create Ticket
            </Link>
          </div>

          {/* Filters */}
          <div className="card p-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Search
                </label>
                <div className="relative">
                  <MagnifyingGlassIcon className="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    placeholder="Search tickets..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="input-field pl-10"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Status
                </label>
                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value as Status | '')}
                  className="input-field"
                >
                  <option value="">All Statuses</option>
                  <option value={Status.OPEN}>Open</option>
                  <option value={Status.IN_PROGRESS}>In Progress</option>
                  <option value={Status.RESOLVED}>Resolved</option>
                  <option value={Status.CLOSED}>Closed</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Priority
                </label>
                <select
                  value={priorityFilter}
                  onChange={(e) => setPriorityFilter(e.target.value as Priority | '')}
                  className="input-field"
                >
                  <option value="">All Priorities</option>
                  <option value={Priority.LOW}>Low</option>
                  <option value={Priority.MEDIUM}>Medium</option>
                  <option value={Priority.HIGH}>High</option>
                  <option value={Priority.URGENT}>Urgent</option>
                </select>
              </div>
            </div>
          </div>

          {/* Tickets List */}
          <div className="card">
            {loading ? (
              <div className="p-6 text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
              </div>
            ) : tickets.length === 0 ? (
              <div className="p-6 text-center text-gray-500">
                <TicketIcon className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                <p>No tickets found</p>
              </div>
            ) : (
              <div className="divide-y divide-gray-200">
                {tickets.map((ticket) => (
                  <div key={ticket.id} className="p-6 hover:bg-gray-50 transition-colors">
                    <div className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center space-x-2 mb-2">
                          <Link
                            href={`/tickets/${ticket.id}`}
                            className="text-lg font-medium text-gray-900 hover:text-primary-600"
                          >
                            #{ticket.id} - {ticket.subject}
                          </Link>
                          <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(ticket.status)}`}>
                            {ticket.status.replace('_', ' ')}
                          </span>
                          <span className={`text-sm font-medium ${getPriorityColor(ticket.priority)}`}>
                            {ticket.priority}
                          </span>
                        </div>
                        <p className="text-gray-600 mb-2 line-clamp-2">{ticket.description}</p>
                        <div className="flex items-center space-x-4 text-sm text-gray-500">
                          <span>
                            Created {new Date(ticket.createdAt).toLocaleDateString()}
                          </span>
                          <span>
                            Updated {new Date(ticket.updatedAt).toLocaleDateString()}
                          </span>
                          {ticket.assignee && (
                            <span>
                              Assigned to {ticket.assignee.firstName} {ticket.assignee.lastName}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default TicketsPage;