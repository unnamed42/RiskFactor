import type { Reducer, Action } from "redux";

interface WriteAction extends Action<"cache/write"> {
  key: string;
  data: unknown;
}

interface RemoveAction extends Action<"cache/remove"> {
  key: string;
}

export const write = (key: string, data: unknown): WriteAction =>
  ({ key, data, type: "cache/write" });

export const remove = (key: string): RemoveAction =>
  ({ key, type: "cache/remove" });

type CacheAction = WriteAction | RemoveAction;

export type CacheState = Record<string, unknown>;

export const reducer: Reducer<CacheState, CacheAction> = (state = {}, action) => {
  switch (action.type) {
    case "cache/write":
      return { ...state, [action.key]: action.data };
    case "cache/remove": {
      const { [action.key]: _, ...rest } = state;
      return rest;
    }
    default:
      return state;
  }
};
