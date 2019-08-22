// general error response
type ApiError = Readonly<{
    timestamp: number,
    status: number,
    error: string,
    message: string,
    stacktrace?: string,
    body?: string
}>;

// post /auth
type ApiToken = Readonly<{
    username: string,
    token: string
}>;

// get /auth
type ApiTokenInfo = Readonly<{
    username: string,
    issued_at: number,
    expire_at: number,
    // property name might change
    // use after checking server-side code
    groups: string[]
}>;
