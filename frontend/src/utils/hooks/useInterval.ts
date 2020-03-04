import { useRef, useEffect } from "react";

type Callback = () => any;

/**
 * 定时器hook。每隔一个延迟执行一边函数。
 * @param fn 要执行的动作
 * @param delay 设定的延迟。当延迟为`null`时，停止定时器。该项可动态调整。
 */
export const useInterval = (fn: Callback, delay: number | null) => {

  // 如果`fn`中使用了state，那么直接使用`fn`闭包的话拿到的永远是上一次渲染时的state，
  // 相当于这个函数不能更新。因此包在ref里。
  const callback = useRef<Callback>();
  useEffect(() => {
    callback.current = fn;
  }, [fn]);

  useEffect(() => {
    // 使用一个函数而非直接`callback.current`的原因同样是为了拿到最新的闭包
    const tick = () => callback.current?.();
    if (delay != null) {
      const id = setInterval(tick, delay);
      return () => clearInterval(id);
    }
  }, [delay]);
};
