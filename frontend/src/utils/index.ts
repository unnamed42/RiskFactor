import { useEffect, useState } from "react";

import { omitBy, isNil } from "lodash";

import { ApiError } from "@/types";

// wrapper around useState, report new value after setState
export const useStateAsync = <T>(init?: T): [T | undefined, (x: T) => Promise<T>] => {
  const [value, setValue] = useState(init);
  return [value, async (x: T) => { setValue(x); return x; }];
};

type LoadableState<State> = { loaded: false } | { loaded: null } | ({ loaded: true } & State);
type OnError = (error: ApiError) => void;

/**
 * 将异步获取到的数据作为初始state，等同于在{@code componentDidMount}中从异步动作中获取初始状态。
 * 提供的更改接口是合并state而非原{@link useState}提供的直接覆盖
 * @param fetch 获取数据的异步api。需要做成函数是因为直接传入promise的话每次组件渲染都会请求一遍这个promise
 * @param onError 发生错误时的处理回调
 * @return loaded + 数据。
 *         loaded === true: 加载完成
 *         loaded === false: 正在加载
 *         loaded === null: 加载时发生错误
 */
export const usePromise = <State>(fetch: () => Promise<State>, onError?: OnError)
  : [LoadableState<State>, (s: Partial<State>) => void] => {
  const [state, setState] = useState<LoadableState<State>>({ loaded: false });

  const updateState = (changes: Partial<State>) => {
    const nextState = { ...state, ...nonNil(changes) };
    setState(nextState);
  };

  useEffect(() => {
    fetch().then(data => setState({ loaded: true, ...data }))
      .catch(e => { onError?.(e); setState({ loaded: null }); });
  }, []);

  return [state, updateState];
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

export const nonNil = <T>(obj: T) => omitBy(obj, isNil);

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
