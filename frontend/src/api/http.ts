import Axios, { AxiosRequestConfig } from "axios";

import jwt_decode from "jwt-decode";
import { baseUrl } from "@/config";
import { local } from "@/api/persist";
import { JWT } from "@/types/auth";

export const http = Axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json"
  }
});

http.interceptors.request.use(config => {
  const { auth: { token } } = local;
  if (token) {
    const jwt = jwt_decode<JWT>(token);
    // Date.getTime() 是微秒
    const now = Math.floor(Date.now() / 1000);
    if(jwt.exp > now)
      config.headers = { ...config.headers, Authorization: `Bearer ${token}` };
  }
  return config;
}, Promise.reject);

/**
 * 向REST API请求数据的通用工具函数
 * @param config 传递给Axios的配置
 */
export const request = <T>(config: AxiosRequestConfig) =>
  http.request<T>({ method: "GET", ...config }).then(({ data }) => data);

export const downloadAsFile = (data: BlobPart, filename: string) => {
  const url = window.URL.createObjectURL(new Blob([data]));
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
};

export default http;
