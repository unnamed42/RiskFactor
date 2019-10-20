// general error response
export interface ApiError {
  timestamp: number;
  status: number;
  error: string;
  message: string;
  stacktrace?: string;
  body?: string;
}

// post /auth
export interface ApiToken {
  username: string;
  token: string;
}

// get /auth
export interface ApiTokenInfo {
  username: string;
  issued_at: number;
  expire_at: number;
  // property name might change
  // use after checking server-side code
  groups: string[];
}
