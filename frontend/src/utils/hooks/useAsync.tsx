import React, { DependencyList, useState, useCallback, useEffect, ReactElement } from "react";
import { Redirect } from "react-router-dom";
import { useDispatch } from "react-redux";

import { message } from "antd";
import type { AxiosError } from "axios";

import { Loading } from "@/components";
import { logout } from "@/redux/auth";
import { write } from "@/redux/cache";
import { store } from "@/redux";
import type { IdType } from "@/api";

interface AsyncLoadedState<T> {
  loading: false;
  data: T;
  error?: undefined;
}

interface AsyncErrorState<E extends Error = Error> {
  loading: false;
  data?: undefined;
  error: E;
}

type AsyncActionState<T> = { loading: true } | AsyncLoadedState<T> | AsyncErrorState;

/**
 * 将一个异步函数包装成hooks，并提供`loading`和`error`辅助属性。
 *
 * @param fn 一个异步函数
 * @param deps 异步函数的依赖列表，用法同`useCallback`
 */
export const useAsync = <T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  deps: DependencyList
): [AsyncActionState<R> | undefined, (...args: T) => Promise<void>] => {

  const [state, setState] = useState<AsyncActionState<R>>();

  // 由于传入的闭包也有`deps`传入作为闭包依赖，所以不把`fn`当作`useCallback`的依赖
  const asyncFunc = useCallback(async (...args: T) => {
    setState({ loading: true });
    try {
      const data = await fn(...args);
      setState({ loading: false, data });
    } catch (error) {
      setState({ loading: false, error });
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  return [state, asyncFunc];
};

interface Options {
  immediate?: boolean;
  reportError?: boolean;
}

const isAxiosError = (error: Error): error is AxiosError =>
  (error as AxiosError).isAxiosError;

/**
 * 将api请求包装为hooks。比`useAsync`多用`message.error`提示错误，以及立即后台开始调用的功能。
 */
export const useApi = <R extends unknown>(
  fn: () => Promise<R>,
  deps: DependencyList,
  { immediate, reportError }: Options
) => {
  const [state, asyncFunc] = useAsync(fn, deps);

  useEffect(() => {
    if (immediate) asyncFunc();
  }, [immediate, asyncFunc]);

  useEffect(() => {
    if (state == undefined || state.loading)
      return;
    if (reportError && state.error)
      message.error(state.error.message);
  }, [state, reportError]);

  return [state];
};

type UseResponse<T> =
  { error: ReactElement | null; state: undefined } |
  { error: undefined; state: AsyncLoadedState<T> }

/**
 * 处理`useAsync`系列函数的返回内容，用一个通用的hook包装。
 *
 * @returns 若加载正常完成，第一个元素包含返回数据，否则为`undefined`。第二个元素若不为`undefined`，则外层
 *          组件需要返回它。
 */
export const useResponse = <T extends unknown>(
  fn: () => Promise<T>, deps: DependencyList
): UseResponse<T> => {

  const dispatch = useDispatch();
  const [state] = useApi(fn, deps, { immediate: true, reportError: true });

  useEffect(() => {
    if (state?.loading == false && state.error !== undefined) {
      const error = state.error;
      // 如果是Unauthorized，则跳转至登录界面重新登录
      if (isAxiosError(error) && error.response?.status === 401)
        dispatch(logout());
    }
  }, [state, dispatch]);

  if (state == undefined)
    return { state: undefined, error: null };
  if (state.loading)
    return { state: undefined, error: <Loading/> };
  if (state.error != undefined) {
    const error = state.error;
    // 如果是Unauthorized，则跳转至登录界面重新登录
    if (isAxiosError(error) && error.response?.status === 401)
      return { state: undefined, error: <Redirect to="/login" /> }
    return { state: undefined, error: null };
  }

  return { state, error: undefined };
};

interface ModifiedAtEntity {
  modifiedAt: number;
  id: IdType;
}

/**
 * 在`useResponse`的基础上，加一层缓存，若请求的对象没有更新，则不向服务器请求完整内容
 *
 * @param cacheKey 缓存内容的唯一标志符，需自行确保唯一性
 * @param modifiedAt 根据请求内容的id和修改时间，向服务器询问是否有更新
 */
export const useCachedResponse = <T extends ModifiedAtEntity>(
  cacheKey: string,
  fn: () => Promise<T>,
  modifiedAt: (id: IdType) => Promise<ModifiedAtEntity["modifiedAt"]>,
  deps: DependencyList
): UseResponse<T> => {
  const dispatch = useDispatch();

  return useResponse(async () => {
    // 不能useSelector，如果用了的话，后续全新数据dispatch时useSelector会重新执行该函数，导致死循环
    const cache = store.getState().cache[cacheKey] as T;
    if (cache !== undefined) {
      const mtime = await modifiedAt(cache.id);
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
  }, [...deps, modifiedAt]);

};
