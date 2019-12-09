/**
 * 获取当前时间。这个时间和java的Date得到的单位一致（毫秒）
 */
export const now = () => Math.floor(Date.now() / 1000);

/**
 * 获取一个object的第一个key（按照key的插入顺序）
 * @param obj object
 */
export const firstKey = (obj: any) => {
  const keys = Object.keys(obj);
  return keys.length !== 0 ? keys[0] : null;
};

/**
 * `setTimeout`延时，Promise版本
 * @param ms 延时，单位 毫秒
 */
export const sleep = (ms: number) =>
  new Promise<void>(resolve => setTimeout(resolve, ms));

/**
 * 利用`requestAnimationFrame`将任务放在后台运行，不阻塞UI的运作
 * @param task 后台任务，需要一个同步函数
 */
export const background = <T, Args extends any[]>(task: (...args: Args) => T | PromiseLike<T>) =>
  (...args: Args) => new Promise<T>(resolve => {
    requestAnimationFrame(() => resolve(task(...args)));
  });

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
 * 返回一个数组，仅作类型转换函数使用。让该数组的类型为`[T1, T2, T3, ...]`而非`(T1 | T2 | T3 | ...)[]`
 *
 * 要求TypeScript 3.1以上
 */
export const tuple = <T extends any[]>(...args: T) => args;

/**
 * 将任意二进制数据当作文件保存在本地，将弹窗获取保存路径
 * @param data 保存的数据
 * @param filename 保存的文件名
 */
export const downloadAsFile = (data: BlobPart, filename: string) => {
  const url = window.URL.createObjectURL(new Blob([data]));
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
};
