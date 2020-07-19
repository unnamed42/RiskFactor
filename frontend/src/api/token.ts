import { rawRequest, request } from "@/api/base";

export interface TokenRequest {
  username: string;
  password: string;
}

interface TokenResponse {
  token: string;
}

/**
 * 用户名密码登录
 */
export const login = (data: TokenRequest): Promise<string> =>
  rawRequest<TokenResponse>({ url: "/token", data, method: "POST" })
    .then(resp => resp.data.token);

/**
 * 刷新token
 */
export const refresh = (): Promise<string> =>
  request<TokenResponse>({ url: "/token" }, false).then(({ token }) => token);
