'use client';

import React, { useEffect, useState } from 'react';
import Layout from '@/components/layout/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { useAuth } from '@/context/AuthContext';
import { useApi } from '@/hooks/useApi';
import { Ticket, PageResponse, Status } from '@/types';
import { hasRole } from '@/lib/auth';
import api from '@/lib/api';
import Link from 'next/link';
import {
  TicketIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  PlusIcon,
} from '@heroicons/react/24/outline';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const { loading, execute } = useApi<PageResponse<Ticket>>();
  const [recentTickets, setRecentTickets] = useState<Ticket[]>([]);
  const [stats, setStats] = useState({
    total: 0,
    open: 0,
    inProgress: 0,
    resolved: 0,
    closed: 0,
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      // Load recent tickets
      const endpoint = hasRole('SUPPORT_AGENT') ? '/tickets' : '/tickets/my';
      const ticketsResponse = await execute(
        () => api.get(`${endpoint}?page=0&size=5&sortBy=createdAt&sortDir=desc`)
      );

      if (ticketsResponse) {
        setRecentTickets(ticketsResponse.content);
        
        // Calculate stats
        const allTicketsResponse = await api.get(`${endpoint}?page=0&size=1000`);
        if (allTicketsResponse.data.success) {
          const allTickets = allTicketsResponse.data.data.content;
          setStats({
            total: allTickets.length,
            open: allTickets.filter((t: Ticket) => t.status === Status.OPEN).length,
            inProgress: allTickets.filter((t: Ticket) => t.status === Status.IN_PROGRESS).length,
            resolved: allTickets.filter((t: Ticket) => t.status === Status.RESOLVED).length,
            closed: allTickets.filter((t: Ticket) => t.status === Status.CLOSED).length,
          });
        }
      }
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
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

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'URGENT':
        return 'text-red-600';
      case 'HIGH':
        return 'text-orange-600';
      case 'MEDIUM':
        return 'text-yellow-600';
      case 'LOW':
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
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
              <p className="text-gray-600">Welcome back, {user?.firstName}!</p>
            </div>
            <Link
              href="/tickets/create"
              className="btn-primary flex items-center"
            >
              <PlusIcon className="h-5 w-5 mr-2" />
              Create Ticket
            </Link>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
            <div className="card p-6">
              <div className="flex items-center">
                <div className="p-2 bg-primary-100 rounded-lg">
                  <TicketIcon className="h-6 w-6 text-primary-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Tickets</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
                </div>
              </div>
            </div>

            <div className="card p-6">
              <div className="flex items-center">
                <div className="p-2 bg-red-100 rounded-lg">
                  <ClockIcon className="h-6 w-6 text-red-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Open</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.open}</p>
                </div>
              </div>
            </div>

            <div className="card p-6">
              <div className="flex items-center">
                <div className="p-2 bg-yellow-100 rounded-lg">
                  <ClockIcon className="h-6 w-6 text-yellow-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">In Progress</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.inProgress}</p>
                </div>
              </div>
            </div>

            <div className="card p-6">
              <div className="flex items-center">
                <div className="p-2 bg-green-100 rounded-lg">
                  <CheckCircleIcon className="h-6 w-6 text-green-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Resolved</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.resolved}</p>
                </div>
              </div>
            </div>

            <div className="card p-6">
              <div className="flex items-center">
                <div className="p-2 bg-gray-100 rounded-lg">
                  <XCircleIcon className="h-6 w-6 text-gray-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Closed</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.closed}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Recent Tickets */}
          <div className="card">
            <div className="px-6 py-4 border-b border-gray-200">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-medium text-gray-900">Recent Tickets</h2>
                <Link
                  href="/tickets"
                  className="text-primary-600 hover:text-primary-700 text-sm font-medium"
                >
                  View all
                </Link>
              </div>
            </div>

            {loading ? (
              <div className="p-6 text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
              </div>
            ) : recentTickets.length === 0 ? (
              <div className="p-6 text-center text-gray-500">
                <TicketIcon className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                <p>No tickets found</p>
              </div>
            ) : (
              <div className="divide-y divide-gray-200">
                {recentTickets.map((ticket) => (
                  <div key={ticket.id} className="p-6 hover:bg-gray-50 transition-colors">
                    <div className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center space-x-2">
                          <Link
                            href={`/tickets/${ticket.id}`}
                            className="text-lg font-medium text-gray-900 hover:text-primary-600"
                          >
                            #{ticket.id} - {ticket.subject}
                          </Link>
                          <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(ticket.status)}`}>
                            {ticket.status.replace('_', ' ')}
                          </span>
                        </div>
                        <p className="text-gray-600 mt-1 line-clamp-2">{ticket.description}</p>
                        <div className="flex items-center space-x-4 mt-2 text-sm text-gray-500">
                          <span className={`font-medium ${getPriorityColor(ticket.priority)}`}>
                            {ticket.priority}
                          </span>
                          <span>
                            Created {new Date(ticket.createdAt).toLocaleDateString()}
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

          {/* Quick Actions */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <Link href="/tickets/create" className="card p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="p-3 bg-primary-100 rounded-lg">
                  <PlusIcon className="h-8 w-8 text-primary-600" />
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-medium text-gray-900">Create Ticket</h3>
                  <p className="text-gray-600">Submit a new support request</p>
                </div>
              </div>
            </Link>

            <Link href="/tickets" className="card p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-center">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <TicketIcon className="h-8 w-8 text-blue-600" />
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-medium text-gray-900">My Tickets</h3>
                  <p className="text-gray-600">View and manage your tickets</p>
                </div>
              </div>
            </Link>

            {hasRole('ADMIN') && (
              <Link href="/admin" className="card p-6 hover:shadow-lg transition-shadow">
                <div className="flex items-center">
                  <div className="p-3 bg-purple-100 rounded-lg">
                    <CheckCircleIcon className="h-8 w-8 text-purple-600" />
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-medium text-gray-900">Admin Panel</h3>
                    <p className="text-gray-600">Manage users and system settings</p>
                  </div>
                </div>
              </Link>
            )}
          </div>
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default DashboardPage;