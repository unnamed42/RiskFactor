import Axios, { AxiosRequestConfig } from "axios";

import { baseUrl } from "@/config";
import { auth } from "@/api/persist";

export const http = Axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json"
  }
});

http.interceptors.request.use(config => {
  const { token, expiry } = auth;
  if (token && expiry > new Date().getTime())
    Object.assign(config.headers, { Authorization: `Bearer ${token}` });
  return config;
}, Promise.reject);

// general error response
export interface Error {
  timestamp: number;
  status: number;
  error: string;
  message: string;
  stacktrace?: string;
  body?: string;
}

/**
 * 向REST API请求数据的通用工具函数
 * @param config 传递给Axios的配置
 */
export const request = async <T = any>(config: AxiosRequestConfig): Promise<T> => {
  if (!config.method)
    config.method = "GET";
  const { data } = await http.request<Error | T>(config);
  if ("error" in data)
    throw data;
  return data;
};

export default http;
