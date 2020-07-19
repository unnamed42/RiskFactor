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

/**
 * 将一个异步函数包装成hooks，并提供`loading`和`error`辅助属性。`loading`表示异步动作是否还在进行，`error`不为`undefined`表示异步动作出现错误。
 *
 * 异步动作在包装之后不会立即开始，需要手动调用。
 *
 * @param fn 一个异步函数。请自行确保`fn`的依赖数据都在`deps`内
 * @param deps 异步函数的依赖列表，用法同`useCallback`
 * @return [asyncState, asyncFunc]
 *         `asyncState`表示当前异步动作的状态，当开始动作后会自动更新，导致组件重新渲染。可能为`undefined`，此时表示异步动作还没开始执行
 *
 *         `asyncFunc`表示被包装后的异步函数，依然是返回Promise的异步函数，需要自己手动调用来开始动作
 */
export const useAsync = <T extends any[], R>(
  fn: (...args: T) => Promise<R>,
  deps: DependencyList
): [ActionState<R> | undefined, (...args: T) => Promise<R | undefined>] => {

  const [state, setState] = useState<ActionState<R>>();

  // 由于传入的闭包也有`deps`传入作为闭包依赖，所以不把`fn`当作`useCallback`的依赖
  const asyncFunc = useCallback((...args: T) => {
    setState({ loading: true });
    return fn(...args).then(
      data => { setState({ loading: false, data }); return data; },
      (error: Error) => {setState({ loading: false, error }); return undefined; }
    );
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  return [state, asyncFunc];
};
