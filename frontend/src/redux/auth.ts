import type { Reducer, Action } from "redux";
// eslint-disable-next-line @typescript-eslint/camelcase
import jwt_decode from "jwt-decode";

import type { IdType } from "@/api";

interface JsonWebToken {
  // 标准头部，issued at（发布时间）
  iat: number;
  // 标准头部，expire at（过期时间）
  exp: number;
  // 标准头部，subject（用作用户名）
  sub: string;
  // 自定义头部，identity（用户id）
  idt: number;
}

interface StoredToken {
  token: string;
  username: string;
  userId: IdType;
  expiry: number;
  issuedAt: number;
}

type LoginAction = Action<"auth/login"> & { token: string };
type LogoutAction = Action<"auth/logout">;

/**
 * 解析jwt当中的数据
 */
const parseToken = (token: string) => {
  const jwt = jwt_decode<JsonWebToken>(token);
  return { username: jwt.sub, userId: jwt.idt, expiry: jwt.exp, issuedAt: jwt.iat };
};

export const login = (token: string): LoginAction =>
  ({ type: "auth/login", token });

export const logout = (): LogoutAction =>
  ({ type: "auth/logout" });

export type AuthState = { token: null } | StoredToken;

type AuthAction = LoginAction | LogoutAction;

export const reducer: Reducer<AuthState, AuthAction> = (state = { token: null }, action) => {
  switch (action.type) {
    case "auth/login":
      return { token: action.token, ...parseToken(action.token) };
    case "auth/logout":
      return { token: null };
    default:
      return state;
  }
};
