import { DependencyList, useState, useCallback } from "react";

interface DefaultState {
  loading: true;
}

interface LoadedState<T> {
  loading: false;
  data: T;
  error?: undefined;
}

interface ErrorState<E extends Error> {
  loading: false;
  data?: undefined;
  error: E;
}

type ActionState<T, E extends Error = Error> =
  DefaultState | LoadedState<T> | ErrorState<E>;

export const isErrorState =
  <T, E extends Error>(state: ActionState<T, E>): state is ErrorState<E> =>
    (state as any).error !== undefined;

/**
 * 将一个异步函数包装成hooks，并提供`loading`和`error`辅助属性。
 *
 * @param fn 一个异步函数
 * @param deps 异步函数的依赖列表，用法同`useCallback`
 */
export const useAsync = <T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  deps: DependencyList
): [ActionState<R> | undefined, (...args: T) => Promise<R | undefined>] => {

  const [state, setState] = useState<ActionState<R>>();

  // 由于传入的闭包也有`deps`传入作为闭包依赖，所以不把`fn`当作`useCallback`的依赖
  const asyncFunc = useCallback(async (...args: T) => {
    setState({ loading: true });
    try {
      console.log("发起请求");
      const data = await fn(...args);
      setState({ loading: false, data });
      return data;
    } catch (error) {
      setState({ loading: false, error });
      return undefined;
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  return [state, asyncFunc];
};
