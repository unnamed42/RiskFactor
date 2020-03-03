import { Reducer } from "redux";

import { omit } from "lodash";
import { merge } from "lodash/fp";

import { Dict, Question, TaskStruct } from "@/types";

enum Actions {
  UPDATE = "task/update",
  INVALIDATE = "task/invalidate"
}

interface Data {
  mtime: number;
  struct?: TaskStruct[];
  layout?: Dict<Question[]>;
}

// taskId -> Data
export type State = Dict<Data>;

interface ReducerAction {
  type: Actions;
  payload: { taskId: string | number } & Partial<Data>;
}

export const update = (taskId: number | string, newData: Data): ReducerAction =>
  ({ type: Actions.UPDATE, payload: { taskId, ...newData } });

export const invalidate = (taskId: number | string): ReducerAction =>
  ({ type: Actions.INVALIDATE, payload: { taskId } });

export const reducer: Reducer<State, ReducerAction> = (state: State = {}, action: ReducerAction) => {
  switch(action.type) {
    case Actions.UPDATE: {
      const { taskId, ...data } = action.payload;
      const savedData = state[taskId];
      if (savedData?.mtime === data.mtime)
        return merge(state, { [taskId]: data });
      else
        return Object.assign(state, { [taskId]: data });
    }
    case Actions.INVALIDATE:
      return omit(state, action.payload.taskId.toString());
    default:
      return state;
  }
};
