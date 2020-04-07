import type { Reducer, Action } from "redux";

import { omit } from "lodash/fp";

interface WriteAction extends Action<"cache/write"> {
  key: string;
  data: any;
}

interface RemoveAction extends Action<"cache/remove"> {
  key: string;
}

export const write = (key: string, data: any): WriteAction =>
  ({ key, data, type: "cache/write" });

export const remove = (key: string): RemoveAction =>
  ({ key, type: "cache/remove" });

type CacheAction = WriteAction | RemoveAction

export type CacheState = any;

export const reducer: Reducer<CacheState, CacheAction> = (state = {}, action) => {
  switch (action.type) {
    case "cache/write":
      return { ...state, [action.key]: action.data };
    case "cache/remove":
      return omit([action.key], state);
    default:
      return state;
  }
};
