import Axios, { AxiosRequestConfig, AxiosResponse } from "axios";

import { baseUrl, refreshThres } from "@/config";
import { store } from "@/redux";
import { login } from "@/redux/auth";
import { refresh } from "./token";

export type IdType = number;
export type ApiIdType = number | string;
export interface IdResponse { id: IdType }

const authentication = () => {
  const state = store.getState().auth;
  if(state.token !== null)
    return state;
  return null;
};

const withBearerToken = (config: AxiosRequestConfig, token: string) =>
  Object.assign(config, { headers: { Authorization: `Bearer ${token}` } });

export const http = Axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json; charset=utf-8"
  }
});

/**
 * 向REST API请求数据的通用工具函数。
 * @param config 请求的配置
 * @template T 请求返回的数据类型
 * @return 请求结果，但依然以`AxiosResponse`的形式返回
 */
export const rawRequest = <T = void>(config: AxiosRequestConfig): Promise<AxiosResponse<T>> =>
  http.request(config);

/**
 * 向REST API请求数据的通用工具函数。与`rawRequest`不同在于，`request`会检查token的时效性，并当快过期时自动刷新
 * @param config 传递给Axios的配置
 * @param refreshToken 是否在token快要过期时刷新
 * @template T 请求返回的数据类型
 * @return 请求结果，直接返回解析完的数据
 */
export const request = async <T = void>(config: AxiosRequestConfig, refreshToken = true): Promise<T> => {
  if(!config.method)
    config.method = "GET";
  const auth = authentication();
  if (auth !== null) {
    // 当快要过期时刷新token
    const { expiry, issuedAt, token } = auth;
    // Date.now()是毫秒，expiry是秒
    const now = Date.now() / 1000;
    const timeRemain = expiry - now, timeTotal = expiry - issuedAt;
    if (timeRemain > 0) {
      // 快要过期（剩余时间占比小于refreshThres）
      if (refreshToken && timeRemain <= timeTotal * refreshThres) {
        const refreshed = await refresh();
        store.dispatch(login(refreshed));
        config = withBearerToken(config, refreshed);
        console.log("expired")
      } else { // 尚且可用
        config = withBearerToken(config, token);
        console.log("usable")
      }
    }
  }
  return (await rawRequest<T>(config)).data;
};
