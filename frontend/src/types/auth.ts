// post /auth
export interface ApiToken {
  token: string;
}

export interface JWT {
  // 标准头部，issued at（发布时间）
  iat: number;
  // 标准头部，expire at（过期时间）
  exp: number;
  // 标准头部，subject（用作用户名）
  sub: string;
  // 自定义头部，identity（用户id）
  idt: number;
}
