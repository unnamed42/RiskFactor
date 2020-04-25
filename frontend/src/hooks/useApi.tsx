import React, { DependencyList, useEffect, ReactElement, useState } from "react";
import { Redirect, useLocation } from "react-router-dom";
import { useDispatch } from "react-redux";

import { message } from "antd";
import type { AxiosError } from "axios";

import { Loading } from "@/components";
import { logout } from "@/redux/auth";
import { write } from "@/redux/cache";
import { store } from "@/redux";
import type { IdType } from "@/api";
import { useMounted, useAsync } from ".";

interface Options {
  immediate?: boolean;
  reportError?: boolean;
  success?: string;
}

export type UseApiDom<T> =
  { alt: ReactElement | null } |
  { alt?: undefined; data: T }

type UseApiReturnType<T extends any[], R> =
  [UseApiDom<R> & { error?: Error }, (...args: T) => Promise<R | undefined>];

const isAxiosError = (error: Error): error is AxiosError =>
  (error as AxiosError).isAxiosError;

/**
 * 将api请求包装为hooks。比`useAsync`多用`message.error`提示错误，以及立即后台开始调用的功能。
 */
export const useApi = <T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  deps: DependencyList,
  options?: Options
): UseApiReturnType<T, R> => {

  const [result, setResult] = useState<UseApiDom<R> & { error?: Error }>({ alt: null });
  const [state, asyncFunc] = useAsync(fn, deps);
  const isMounted = useMounted();
  const dispatch = useDispatch();
  const location = useLocation();

  const { immediate = true, reportError = true, success } = options ?? {};

  useEffect(() => {
    if (immediate)
      (asyncFunc as any)();
  }, [immediate, asyncFunc]);

  useEffect(() => {
    if (!isMounted() || state == undefined || state.loading)
      return;
    if (state.error) {
      if (reportError)
        message.error(state.error.message);
    } else if (success !== undefined)
      message.success(success);
  // 无视`isMounted`的deps警告
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state, reportError]);

  useEffect(() => {
    if (!isMounted())
      return;
    if (state?.loading == false && state.error !== undefined) {
      const error = state.error;
      // 如果是Unauthorized，则跳转至登录界面重新登录
      if (isAxiosError(error) && error.response?.status === 401)
        dispatch(logout());
      else if (process.env.NODE_ENV === "development")
        console.log(error);
    }
  // 无视`isMounted`的deps警告
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state, dispatch]);

  useEffect(() => {
    if (state == undefined)
      setResult({ alt: null });
    else if (state.loading)
      setResult({ alt: <Loading /> });
    else if (state.error != undefined) {
      const error = state.error;
      // 如果是Unauthorized，则跳转至登录界面重新登录
      if (isAxiosError(error) && error.response?.status === 401)
        setResult({ error: state.error, alt: <Redirect to={{ pathname: "/login", state: { from: location } }} /> })
      setResult({ error: state.error, alt: null });
    } else
      setResult({ data: state.data });
  }, [state, location]);

  return [result, asyncFunc];
};



interface Entity {
  id: IdType;
  modifiedAt: number;
}

interface CachableOptions extends Options {
  cacheKey: string;
  mtimeGetter: (id: Entity["id"]) => Promise<Entity["modifiedAt"]>;
}

/**
 * 在`useApi`的基础上，加一层缓存，若请求的对象没有更新，则不向服务器请求完整内容，
 * 而是从`store`中读取上一次获得的数据。
 */
export const useApiCached = <T extends Entity>(
  fn: () => Promise<T>,
  deps: DependencyList,
  options: CachableOptions
): UseApiReturnType<[], T> => {
  const dispatch = useDispatch();
  const { cacheKey, mtimeGetter } = options;

  return useApi(async () => {
    // 不能useSelector。后续获取全新数据dispatch时useSelector导致重渲染会重新执行到这里，导致死循环
    const cache = store.getState().cache[cacheKey] as T;
    if (cache !== undefined) {
      const mtime = await mtimeGetter(cache.id);
      console.log(`缓存 ${cacheKey} 更新于 ${cache.modifiedAt}，服务器值更新于 ${mtime}`);
      if (cache.modifiedAt >= mtime) {
        console.log(`自缓存取得 ${cacheKey}`);
        return cache;
      }
    }
    const latest = await fn();
    dispatch(write(cacheKey, latest));
    console.log(`重新获取 ${cacheKey}`);
    return latest;
  }, [...deps, cacheKey], options);
};
