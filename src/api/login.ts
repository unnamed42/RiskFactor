import { request } from "@/api/http";

import { ApiToken } from "@/types/auth";

/**
 * 用户名密码登录
 */
export const login = (username: string, password: string) =>
  request<ApiToken>({ url: "/login", data: { username, password }, method: "POST" })
    .then(({ token }) => token);

/**
 * 退出登录不需要与后台API交互，直接在本地删除token即可
 * 该logout是空操作，为与其他api保持一致返回Promise<void>
 */
export const logout = () => Promise.resolve();
