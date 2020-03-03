import Axios, { AxiosRequestConfig } from "axios";

import { merge } from "lodash";

import { baseUrl, refreshThres } from "@/config";
import { store } from "@/redux";
import { login } from "@/redux/auth";
import { now } from "@/utils";
import { refresh } from "./auth";

const authentication = () => {
  const state = store.getState().auth;
  if(state.token !== null)
    return state;
  return null;
};

const withBearerToken = (config: AxiosRequestConfig, token: string) =>
  merge(config, { headers: { Authorization: `Bearer ${token}` } });

export const http = Axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json; charset=utf-8"
  }
});

// 当快要过期时刷新token
http.interceptors.request.use(async config => {
  const auth: string | undefined = config.headers?.Authorization;
  // 当前请求是携带了token的。未携带token则不需要刷新token
  if(auth && auth.startsWith("Bearer ")) {
    // 如果携带token则必然是通过token合法性验证的，一定不是null
    const { expiry, issuedAt, token } = authentication()!;
    const timeRemain = expiry - now(), timeTotal = expiry - issuedAt;
    // 快要过期（剩余时间占比小于refreshThres）
    if(0 < timeRemain && timeRemain <= timeTotal * refreshThres) {
      const refreshed = await refresh(token);
      store.dispatch(login(refreshed));
      withBearerToken(config, refreshed);
    }
  }
  return config;
}, e => Promise.reject(e));

/**
 * 向REST API请求数据的通用工具函数
 * @param config 传递给Axios的配置
 * @param withToken 当token可用时是否携带token数据，默认为`true`
 * @template T 请求返回的数据类型
 */
export const request = async <T>(config: AxiosRequestConfig, withToken = true) => {
  if(!config.method)
    config.method = "GET";
  if(withToken) {
    const auth = authentication();
    if(auth !== null && auth.expiry > now())
      withBearerToken(config, auth.token);
  }
  return (await http.request<T>(config)).data;
};
