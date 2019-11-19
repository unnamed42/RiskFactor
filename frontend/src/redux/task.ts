import { KVPair, Question } from "@/types";
import { without } from "@/utils";

enum Actions {
  STORE = "task/store",
  INVALIDATE = "task/invalidate"
}

interface Data {
  mtime: number;
  layout: Map<string, Question[]>;
}

// taskId -> Data
export type State = KVPair<Data>;

interface Payload extends Partial<Data> {
  taskId: string | number;
}

interface ReducerAction {
  type: Actions;
  payload: Payload;
}

export const update = (taskId: number | string, mtime: Data["mtime"], layout: Data["layout"]): ReducerAction =>
  ({ type: Actions.STORE, payload: { taskId, mtime, layout } });

export const invalidate = (taskId: number | string): ReducerAction =>
  ({ type: Actions.INVALIDATE, payload: { taskId } });

export const reducer = (state: State = {}, action: ReducerAction) => {
  const { type, payload } = action;
  switch(type) {
    case Actions.STORE:
      const { taskId, ...data } = payload;
      return { ...state, [taskId]: data };
    case Actions.INVALIDATE:
      return without(state, payload.taskId.toString());
    default:
      return state;
  }
};
