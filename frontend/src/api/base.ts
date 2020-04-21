import Axios, { AxiosRequestConfig } from "axios";

import { baseUrl, refreshThres } from "@/config";
import { store } from "@/redux";
import { login } from "@/redux/auth";
import { now } from "@/utils";
import { refresh } from "./token";

export type IdType = number;
export interface Dict<T = string> { [key: string]: T }
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

export const rawRequest = <T = void>(config: AxiosRequestConfig) =>
  http.request<T>(config);

/**
 * 向REST API请求数据的通用工具函数
 * @param config 传递给Axios的配置
 * @param refreshToken 是否在token快要过期时刷新
 * @template T 请求返回的数据类型
 */
export const request = async <T = void>(config: AxiosRequestConfig, refreshToken = true) => {
  if(!config.method)
    config.method = "GET";
  const auth = authentication();
  if (auth !== null) {
    // 当快要过期时刷新token
    const { expiry, issuedAt, token } = auth;
    // now是毫秒，expiry是秒
    const timeRemain = expiry - now() / 1000, timeTotal = expiry - issuedAt;
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
