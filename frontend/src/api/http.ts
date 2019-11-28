import Axios, { AxiosRequestConfig } from "axios";

import { assign } from "lodash";

import { baseUrl } from "@/config";
import { store } from "@/redux";
import { now } from "@/utils";

export const http = Axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json; charset=utf-8"
  }
});

http.interceptors.request.use(config => {
  const auth = store.getState().auth;
  if(auth.token !== null && auth.expiry > now())
    assign(config.headers, { Authorization: `Bearer ${auth.token}` });
  return config;
}, Promise.reject);

/**
 * 向REST API请求数据的通用工具函数
 * @param config 传递给Axios的配置
 * @typeparam <T> 请求返回的数据类型
 */
export const request = <T>(config: AxiosRequestConfig): Promise<T> =>
  http.request<T>({ method: "GET", ...config }).then(({ data }) => data);
