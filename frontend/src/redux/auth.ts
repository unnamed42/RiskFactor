import { Reducer } from "redux";

import jwt_decode from "jwt-decode";
import { JWT } from "@/types/auth";

/**
 * 标记reducer中动作
 */
enum Actions {
  LOGIN = "auth/login",
  LOGOUT = "auth/logout",
}

/**
 * reducer状态
 */
export type State = { token: null } | {
  token: string;
  username: string;
  userId: number;
  expiry: number;
};

interface ActionType {
  type: Actions;
  payload: State;
}

/**
 * 解析jwt当中的数据
 */
const parseToken = (token: string) => {
  const jwt = jwt_decode<JWT>(token);
  return { username: jwt.sub, userId: jwt.idt, expiry: jwt.exp };
};

export const login = (token: string) =>
  ({ type: Actions.LOGIN, payload: { token, ...parseToken(token) } });

export const logout = () =>
  ({ type: Actions.LOGOUT, payload: {} });

export const reducer: Reducer<State, ActionType> = (state: State = { token: null }, action: ActionType) => {
  switch (action.type) {
    case Actions.LOGIN:
      return { ...action.payload };
    case Actions.LOGOUT:
      return { token: null };
    default:
      return state;
  }
};
