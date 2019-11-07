import { useEffect, useState } from "react";

import { message } from "antd";

import { ApiError } from "@/types";

// wrapper around useState, report new value after setState
export const useStateAsync = <T>(init?: T): [T | undefined, (x: T) => Promise<T>] => {
  const [value, setValue] = useState(init);
  return [value, async (x: T) => { setValue(x); return x; }];
};

interface UsePromiseState<T> {
  value?: T;
  loaded: boolean;
  error?: string;
}

type OnError = (error: ApiError) => void;

/**
 * 将异步获取到的数据作为初始state，等同于在componentDidMount中从异步动作中获取初始状态。该state不提供更改接口（也不应该需要更改）
 * @param api 获取数据的异步api
 * @param onError 发生错误时的处理回调。将Antd.message放在这里而非组件的render中是为了避免"nested component update"错误
 *                为null时代表忽略错误处理
 */
export const usePromise = <T>(api: Promise<T>, onError?: OnError | null): UsePromiseState<T> => {
  const [state, setState] = useState<UsePromiseState<T>>({ loaded: false });

  const onErrorDefault: OnError = e => message.error(e.message);

  useEffect(() => {
    api.then(value => setState({ loaded: true, value })).catch((err: ApiError) => {
      setState({ loaded: true, error: err.error });
      if(onError === undefined)
        onError = onErrorDefault;
      if(onError !== null)
        onError(err);
    });
  }, []);

  return state;
};

export const sleep = (ms: number) =>
  new Promise<void>(resolve => setTimeout(resolve, ms));