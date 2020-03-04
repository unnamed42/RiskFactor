import { useRef, useEffect } from "react";

/**
 * 检测组件是否已经mount的hook.
 * @return 一个`() => boolean`函数，返回含义是isMounted.
 */
export const useMounted = (): (() => boolean) => {
  const mounted = useRef(false);
  useEffect(() => {
    mounted.current = true;
    return () => { mounted.current = false };
  }, []);
  return () => mounted.current;
};
