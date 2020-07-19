import { useRef, useEffect } from "react";

/**
 * 检测组件是否已经加载的hook。
 *
 * @return 无参数，返回bool的一个函数，含义为当前组件是否mounted。
 *         该函数可以认为是重新渲染也保持不变的量，因此用到时可以不放进DependencyList中去。
 */
export const useMounted = (): () => boolean => {
  const mounted = useRef(false);

  useEffect(() => {
    mounted.current = true;
    return () => { mounted.current = false; }
  }, []);

  return () => mounted.current;
};
