// general error response
export interface ApiError {
  timestamp: number;
  status: number;
  error: string;
  message: string;
  stacktrace?: string;
  body?: string;
}

export interface IdResponse {
  id: string | number;
}

export interface KVPair<T> {
  [key: string]: T;
}

export * from "./auth";
export * from "./task";
