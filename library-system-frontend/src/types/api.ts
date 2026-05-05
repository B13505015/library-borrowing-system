// Future Java backend response wrapper
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

export class ApiError extends Error {
  status?: number;
  constructor(message: string, status?: number) {
    super(message);
    this.status = status;
  }
}
