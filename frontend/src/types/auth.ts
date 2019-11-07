// post /auth
export interface ApiToken {
  token: string;
}

export interface JWT {
  iat: number;
  exp: number;
  sub: string;
  idt: number;
}
