import { useEffect, useState } from "react";

import { omitBy, isNil } from "lodash";

import { ApiError } from "@/types";

type LoadableState<State> = { loaded: false } | { loaded: null } | ({ loaded: true } & State);
type OnError = (error: ApiError) => void;

/**
 * 将异步获取到的数据作为初始state，等同于在`componentDidMount`中从异步动作中获取初始状态。
 * 提供的更改接口是合并state而非原`useState`提供的直接覆盖
 * @param fetch 获取数据的异步api。需要做成函数是因为直接传入promise的话每次组件渲染都会请求一遍这个promise
 * @param onError 发生错误时的处理回调
 * @return loaded + 数据。
 *         loaded === true: 加载完成
 *         loaded === false: 正在加载
 *         loaded === null: 加载时发生错误
 */
export const usePromise = <State extends any>(fetch: () => Promise<State>, onError?: OnError)
  : [LoadableState<State>, (s: Partial<State>) => void] => {
  const [state, setState] = useState<LoadableState<State>>({ loaded: false });

  const updateState = (changes: Partial<State>) => {
    const nextState = { ...state, ...omitBy(changes, isNil) };
    setState(nextState);
  };

  useEffect(() => {
    fetch().then(data => setState({ loaded: true, ...data }))
      .catch(e => { onError?.(e); setState({ loaded: null }); });
  }, []);

  return [state, updateState];
};

