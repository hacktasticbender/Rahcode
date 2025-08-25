export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum Role {
  USER = 'USER',
  SUPPORT_AGENT = 'SUPPORT_AGENT',
  ADMIN = 'ADMIN'
}

export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

export enum Status {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  RESOLVED = 'RESOLVED',
  CLOSED = 'CLOSED'
}

export interface Ticket {
  id: number;
  subject: string;
  description: string;
  priority: Priority;
  status: Status;
  requester: User;
  assignee?: User;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  closedAt?: string;
  comments?: Comment[];
  attachments?: Attachment[];
  rating?: Rating;
}

export interface Comment {
  id: number;
  content: string;
  user: User;
  createdAt: string;
  updatedAt: string;
}

export interface Attachment {
  id: number;
  fileName: string;
  contentType: string;
  fileSize: number;
  uploadedBy: User;
  uploadedAt: string;
}

export interface Rating {
  id: number;
  stars: number;
  feedback?: string;
  user: User;
  createdAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface CreateTicketRequest {
  subject: string;
  description: string;
  priority: Priority;
}

export interface UpdateTicketRequest {
  subject?: string;
  description?: string;
  priority?: Priority;
  status?: Status;
  assigneeId?: number;
}

export interface CreateCommentRequest {
  content: string;
}

export interface CreateRatingRequest {
  stars: number;
  feedback?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}