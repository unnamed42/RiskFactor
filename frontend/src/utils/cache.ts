import { assign, flatMap } from "lodash";

import { store } from "@/redux";
import { State as TaskState, update } from "@/redux/task";
import { taskLayout, taskMtime, taskStructure } from "@/api/task";
import { Dict, Question } from "@/types";

// 将所有内容归约为 { [一级标题/二级标题/三级标题...]: 问题list, ... }
const formatLayout = (questions: Question[]): Dict<Question[]> => {
  // 默认第一层的Question type应该为header
  // 将第一层map成 { header的label: header的list } 形式，成一个数组
  let init = questions.map(q => {
    if(q.type !== "header" || !q.list)
      throw new Error("questions in first layer is not a valid header");
    return { title: q.label ?? "", list: q.list };
  });
  // 如果list中还有header，也将其展开成上述格式并合并到数组中来
  for(;;) {
    let changed = false;
    init = flatMap(init, elem => {
      const { title, list } = elem;
      if(list[0].type !== "header")
        return elem;
      changed = true;
      // 只要list中有一个是header，那么认为都是header
      return list.map(q => {
        if(q.type !== "header" || !q.list)
          throw new Error("question is not a header");
        return { title: `${title}/${q.label ?? ""}`, list: q.list };
      });
    });
    if(!changed) break;
  }
  // 将 { title, list } 数组合并成Map
  return init.reduce((obj: Dict<Question[]>, { title, list }) => assign(obj, { [title]: list }), {});
};


type State = NonNullable<TaskState[keyof TaskState]>;

/**
 * 自redux store中获取缓存的api数据。不存在或失效时重新获取（要求后端提供一个`mtime`修改时间的api）
 * @param taskId 项目id
 * @param prop 要获取的缓存的属性名称
 * @param fetch 缓存不存在时，获取数据的异步操作（一般是从api GET）
 */
export const cacheSelector = async <K extends keyof State, T extends State[K]>
(taskId: number | string, prop: K, fetch: () => Promise<T>): Promise<T> => {
  const { mtime } = await taskMtime(taskId);
  const cached = (store.getState().task)[taskId];
  if(cached !== undefined && cached.mtime === mtime && cached[prop] !== undefined) {
    // @ts-ignore
    return cached[prop];
  } else {
    const result = await fetch();
    store.dispatch(update(taskId, { mtime, [prop]: result }));
    return result;
  }
};

export const cachedLayout = (taskId: number | string) =>
  cacheSelector(taskId, "layout", () => taskLayout(taskId).then(formatLayout));

export const cachedStructure = (taskId: number | string) =>
  cacheSelector(taskId, "struct", () => taskStructure(taskId));
