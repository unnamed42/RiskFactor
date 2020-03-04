import { DependencyList, useState, useCallback } from "react";

type AsyncActionState<T> = {
  loading: true;
} | {
  loading: false;
  data: T;
  error?: undefined;
} | {
  loading: false;
  data?: undefined;
  error: Error;
};

/**
 * 将一个异步函数包装成hooks，并提供`loading`和`error`辅助属性。
 * @param fn 一个异步函数
 * @param deps 异步函数的依赖列表，用法同`useCallback`
 */
export const useAsync = <T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  deps: DependencyList
): [AsyncActionState<R> | undefined, (...args: T) => Promise<void>] => {

  const [state, setState] = useState<AsyncActionState<R>>();

  const asyncFunc = useCallback((...args: T) => {
    setState({ loading: true });
    return fn(...args).then(
      data => setState({ loading: false, data }),
      error => setState({ loading: false, error })
    );
  }, deps);

  return [state, asyncFunc];
}
