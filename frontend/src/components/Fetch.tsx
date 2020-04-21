import React, { useEffect, ReactElement, useState } from "react";
import { useDispatch } from "react-redux";
import { Redirect, useLocation } from "react-router-dom";
import type { AxiosError } from "axios";

import { message } from "antd";

import { Loading } from "./Loading";
import { useAsync } from "@/utils";
import { store } from "@/redux";
import { logout } from "@/redux/auth";
import { write } from "@/redux/cache";
import type { IdType } from "@/api";

interface ChildProps<T> {
  data: T;
  setData: (prevData: T) => void;
}

interface DefaultProps<Result> {
  fetch: () => Promise<Result>;
  children: (props: ChildProps<Result>) => ReactElement;
  placeholder?: ReactElement;
}

interface Checkable {
  id: IdType;
  modifiedAt: number;
}

interface CacheableProps {
  cacheKey: string;
  updated: (id: IdType) => Promise<number>;
}

type P<T> = DefaultProps<T> & (T extends Checkable ? Partial<CacheableProps> : {});

const isAxiosError = (err: Error): err is AxiosError =>
  (err as Partial<AxiosError>).isAxiosError == true;

const isUnauthorized = (err: Error) =>
  isAxiosError(err) && err.response?.status == 401;

export function Fetch<R>({ fetch, children, placeholder, ...rest }: P<R>): ReactElement | null {

  const dispatch = useDispatch();
  const location = useLocation();

  const { cacheKey, updated } = (rest as Partial<CacheableProps>);

  const [state, fetchFn] = useAsync(async () => {
    if (cacheKey !== undefined) {
      // 不能useSelector。后续获取全新数据dispatch时useSelector触发重渲染又会重新执行到这里，导致死循环
      const cache = store.getState().cache[cacheKey] as (R & Checkable);
      if (cache !== undefined) {
        // 没设置更新日期检查，则认为是永久缓存
        if (updated === undefined)
          return cache;
        const mtime = await updated(cache.id);
        console.log(`缓存 ${cacheKey} 更新于 ${cache.modifiedAt}，服务器值更新于 ${mtime}`);
        if (cache.modifiedAt >= mtime) {
          console.log(`自缓存取得 ${cacheKey}`);
          return cache;
        }
      }
    }
    return await fetch();
  }, [cacheKey, updated, dispatch]);

  const [v, setV] = useState<R>();

  useEffect(() => {
    fetchFn();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (state && !state.loading) {
      if (state.error != undefined) {
        message.error(state.error);
        if (isUnauthorized(state.error))
          dispatch(logout());
      }
      else if (state.data != undefined) {
        setV(prev => prev ?? state.data);
        if(cacheKey !== undefined)
          dispatch(write(cacheKey, state.data));
      }
    }
  }, [state, cacheKey, dispatch]);

  if (state === undefined || state.loading)
    return placeholder ?? <Loading />;
  if (v == undefined)
    return null;
  if (state.error)
    return isUnauthorized(state.error) ?
      <Redirect to={{ pathname: "/login", state: { from: location } }} /> :
      null;

  return children({
    data: v,
    setData: setV
  });
}
