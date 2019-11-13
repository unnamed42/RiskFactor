import { Section } from "@/types";
import { without } from "@/utils";

enum Actions {
  STORE = "task/store",
  INVALIDATE = "task/invalidate"
}

export interface State {
  [taskId: string]: {
    mtime: number;
    layout: Section[];
  };
}

interface Payload {
  taskId: string | number;
  mtime?: number;
  layout?: Section[];
}

interface ReducerAction {
  type: Actions;
  payload: Payload;
}

export const update = (taskId: number | string, mtime: number, layout: Section[]): ReducerAction =>
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
