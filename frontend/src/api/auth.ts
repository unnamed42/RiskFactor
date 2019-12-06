import { request } from "@/api/http";

import { ApiToken } from "@/types/auth";

/**
 * 用户名密码登录
 */
export const login = (username: string, password: string) =>
  request<ApiToken>({ url: "/login", data: { username, password }, method: "POST" })
    .then(({ token }) => token);

/**
 * 刷新token
 */
export const refresh = (token: string) =>
  request<ApiToken>({ url: "/login/refresh", data: { token } }).then(({ token }) => token);
