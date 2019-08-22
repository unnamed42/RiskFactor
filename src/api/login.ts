import { request } from "@/api/http";

type LoginRequest = Readonly<{
    username: string,
    password: string
}>;

async function login(payload: LoginRequest): Promise<ApiToken> {
    return await request<ApiToken>({
        url: "/auth", data: payload, method: "POST"
    });
}

async function tokenInfo(): Promise<ApiTokenInfo> {
    return await request<ApiTokenInfo>({
        url: "/auth", method: "GET"
    });
}

function logout(): void {
    // nothing here
    // localStorage handling is all kept in store
}

export {
    login, logout, tokenInfo
};
