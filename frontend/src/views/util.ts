import { store } from "@/redux";
import { State as TaskState, update } from "@/redux/task";
import { taskMtime } from "@/api/task";

type State = NonNullable<TaskState[keyof TaskState]>;

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
