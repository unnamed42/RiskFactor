import { omitBy, isNil } from "lodash";

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

export const tuple = <T extends any[]>(...args: T) => args;
