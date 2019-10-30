import { request } from "@/api/http";

import { ApiToken, ApiTokenInfo } from "@/types/auth";

/**
 * 用户名密码登录
 */
export const login = async (username: string, password: string) =>
  await request<ApiToken>({ url: "/auth", data: { username, password }, method: "POST" });

/**
 * 登录后请求当前登录token的详细信息（用户名，过期时间）
 */
export const tokenInfo = async () =>
  await request<ApiTokenInfo>({ url: "/auth" });

/**
 * 退出登录不需要与后台API交互，直接在本地删除token即可
 * 该logout是空操作，为与其他api保持一致返回Promise<void>
 */
export const logout = () => Promise.resolve();
