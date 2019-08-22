import Axios, { AxiosRequestConfig } from "axios";

import { auth } from "@/api/persist";

const http = Axios.create({
    baseURL: process.env.API_BASE,
    headers: {
        "Content-Type": "application/json"
    }
});

http.interceptors.request.use(config => {
    const { token, expiry } = auth;
    if (token && expiry > new Date().getTime())
        Object.assign(config.headers, {Authorization: `Bearer ${token}`});
    return config;
}, err => Promise.reject(err));

async function request<T>(config: AxiosRequestConfig): Promise<T> {
    const response = await http.request<ApiError | T>(config);
    const reply = response.data;
    if("error" in reply)
        throw reply;
    return reply;
}

export default http;
export { request };
