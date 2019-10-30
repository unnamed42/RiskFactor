import { request } from "@/api/http";

import { ApiToken, ApiTokenInfo } from "@/types/auth";

export * from "@/types/auth";

export type LoginPayload = Readonly<{
  username: string,
  password: string
}>;

export const login = async (payload: LoginPayload) =>
  await request<ApiToken>({ url: "/auth", data: payload, method: "POST" });

export const tokenInfo = async () =>
  await request<ApiTokenInfo>({ url: "/auth" });

/**
 * 退出登录不需要与后台API交互，直接在本地删除token即可
 * 该logout是空操作，为与其他api保持一致返回Promise<void>
 */
export const logout = () => Promise.resolve();
