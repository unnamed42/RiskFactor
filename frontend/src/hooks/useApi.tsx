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

type UseApiDomPartial<T> =
  { alt: ReactElement | null } | { alt?: undefined; data: T };

export type UseApiDom<T> = UseApiDomPartial<T> & { error?: Error };

type UseApiReturnType<T extends any[], R> =
  [UseApiDom<R>, (...args: T) => Promise<R | undefined>];

const isAxiosError = (error: Error): error is AxiosError =>
  (error as AxiosError).isAxiosError;

/**
 * 由于当`Options.immediate`设置为`true`时，`useApi`的`asyncFunc`不接受任何参数，
 * 因此创建这个type guard以绕过类型检查
 */
const isImmediateCallback = <T extends any[], R>(func: (...args: T) => R, immediate: boolean): func is () => R =>
  immediate;

/**
 * 将api请求包装为hooks。在`useAsync`基础上继续包装，比`useAsync`多用`message.error`提示错误，
 * 以及立即后台开始调用的功能。
 *
 * @param fn 最好是`src/api`里的那些REST API请求函数，其他函数也可以。请自行确保`fn`的依赖数据都在`deps`内
 * @param deps 异步函数的依赖列表，用法同`useCallback`
 * @param options 异步动作的选项，有三个：
 *          immediate: 异步动作是否在hook创建时立即开始执行。若为true，则fn应该为一个0参数函数
 *          reportError: 异步动作出错时是否使用`message.error`报错
 *          success: 成功时弹出的提示文字。为undefined则不提示
 * @return [response, asyncFunc]
 *          response: 异步函数的结果状态，异步动作未开始时为undefined。具有三个属性：
 *            alt: 为一个ReactElement。当异步函数正在执行中或者出现错误时不为undefined，此时外层组件应该返回它的内容作为渲染结果。
 *            error: 当异步函数出现错误时，捕获的异常。没有错误则为undefined
 *            data: 异步函数正常结束时返回的数据。当alt和error都为undefined时才可用
 *          asyncFunc: 同`useAsync`的返回结果
 */
export const useApi = <T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  deps: DependencyList,
  options?: Options
): UseApiReturnType<T, R> => {

  const [result, setResult] = useState<UseApiDom<R>>({ alt: null });
  const [state, asyncFunc] = useAsync(fn, deps);
  const isMounted = useMounted();
  const dispatch = useDispatch();
  const location = useLocation();

  const { immediate = true, reportError = true, success } = options ?? {};

  useEffect(() => {
    if (isImmediateCallback(asyncFunc, immediate))
      void asyncFunc();
  }, [immediate, asyncFunc]);

  useEffect(() => {
    if (!isMounted() || state == undefined || state.loading)
      return;
    if (state.error) {
      if (reportError)
        void message.error(state.error.message);
    } else if (success !== undefined)
      void message.success(success);
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
 *
 * @param fn 同上
 * @param deps 同上
 * @param options 在`useApi`的options的基础上，添加了两个属性：
 *          cacheKey: 用于标记缓存的键值，自行保证唯一性
 *          mtimeGetter: 请求对象是否发生更新的动作，即根据对象的id获取其最近修改时间。
 * @template T 要求T必须具有`Entity`的两个属性
 * @return 同上
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
  }, [...(deps as unknown[]), cacheKey], options);
};
