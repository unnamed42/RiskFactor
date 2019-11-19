import { DependencyList, useEffect, useState } from "react";

import { message } from "antd";

import { ApiError } from "@/types";

// wrapper around useState, report new value after setState
export const useStateAsync = <T>(init?: T): [T | undefined, (x: T) => Promise<T>] => {
  const [value, setValue] = useState(init);
  return [value, async (x: T) => { setValue(x); return x; }];
};

interface UPInitState {
  loaded: false;
}

interface UPLoadedState<T> {
  loaded: true;
  value: T;
}

interface UPErrorState {
  loaded: true;
  error: string;
}

type UsePromiseState<T> = UPInitState | UPLoadedState<T> | UPErrorState;

type OnError = (error: ApiError) => void;

/**
 * 将异步获取到的数据作为初始state，等同于在componentDidMount中从异步动作中获取初始状态。该state不提供更改接口（也不应该需要更改）
 * @param api 获取数据的异步api。需要做成函数是因为直接传入promise的话每次组件渲染都会请求一遍这个promise
 * @param onError 发生错误时的处理回调。将Antd.message放在这里而非组件的render中是为了避免"nested component update"错误
 *                为null时代表忽略错误处理
 */
export const usePromise = <T>(api: () => Promise<T>, onError?: OnError | null): UsePromiseState<T> => {
  const [state, setState] = useState<UsePromiseState<T>>({ loaded: false });

  const onErrorDefault: OnError = e => message.error(e.message);

  useEffect(() => {
    api().then(value => setState({ loaded: true, value })).catch((err: ApiError) => {
      setState({ loaded: true, error: err.error });
      if(onError === undefined)
        onError = onErrorDefault;
      if(onError !== null)
        onError(err);
    });
  }, []);

  return state;
};

export const useEffectAsync = (callback: () => Promise<void>, deps?: DependencyList) => {
  useEffect(() => { callback(); }, deps);
};

/**
 * 获取当前时间。这个时间和java的Date得到的单位一致（毫秒）
 */
export const now = () => Math.floor(Date.now() / 1000);

export const firstKey = (obj: any) => {
  const keys = Object.keys(obj);
  return keys.length !== 0 ? keys[0] : null;
};

export const sleep = (ms: number) =>
  new Promise<void>(resolve => setTimeout(resolve, ms));

/**
 * 将一个数组的内容合并到另一个数组中去，不创建新数组
 * @param dest 合并的目标
 * @param src 要合并的数据
 * @return 修改后的合并目标，并未创建新对象
 */
export const appendArray = <T>(dest: T[], src: T[]) => {
  const dstLen = dest.length, srcLen = src.length;
  dest.length = dstLen + srcLen;
  for(let i=0; i<srcLen; ++i)
    dest[dstLen + i] = src[i];
  return dest;
};

/**
 * 除了某个属性之外，复制一个新的Object
 * @param obj 源Object
 * @param key 要排除的属性
 */
export const without = (obj: any, key: string) =>
  Object.assign({}, ...(Object.entries(obj).filter(([k, _]) => k !== key)));
