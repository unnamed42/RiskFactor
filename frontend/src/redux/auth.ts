import { Action } from "redux";
import { ThunkAction } from "redux-thunk";

import jwt_decode from "jwt-decode";
import * as api from "@/api/login";
import { local } from "@/api/persist";
import { JWT } from "@/types/auth";

///
/// types
///

/**
 * 标记reducer中动作
 */
enum AuthAction {
  LOGOUT = "auth/logout",
  // types for async loading
  LOGIN_POSTING = "auth/login-posting",
  LOGIN_SUCCESS = "auth/login-success",
  LOGIN_FAILURE = "auth/login-failure",
}

/**
 * reducer状态
 */
export interface State {
  username?: string;
  userId?: number;
  expiry?: number;
}

///
/// actions
///
interface LoginPayload {
  username: string;
  password: string;
  remember: boolean;
}

interface AuthActionType {
  type: AuthAction;
  payload?: State;
}

type AuthActionThunk = ThunkAction<void, State, null, Action<AuthAction>>;

/**
 * 解析jwt当中的数据
 */
const parseToken = (token: string): State => {
  const jwt = jwt_decode<JWT>(token);
  return { username: jwt.sub, userId: jwt.idt, expiry: jwt.exp };
};

export const login = (payload: LoginPayload, onLoginSuccess?: () => void): AuthActionThunk => async dispatch => {
  dispatch({ type: AuthAction.LOGIN_POSTING });
  try {
    const token = await api.login(payload.username, payload.password);
    // this is needed for http requests
    local.auth.token = token;
    dispatch({ type: AuthAction.LOGIN_SUCCESS, payload: { token, ...parseToken(token) } });
    if (onLoginSuccess) onLoginSuccess();
  } catch (e) {
    const error = e as Error;
    dispatch({ type: AuthAction.LOGIN_FAILURE, payload: { error: error.message } });
  }
};

export const logout = (): AuthActionType => {
  local.auth.token = null;
  return { type: AuthAction.LOGOUT, payload: {} };
};

///
/// reducers
///
const initState = (() => {
  const token = local.auth.token;
  return token ? { token, ...parseToken(token) } : {};
})();

export const reducer = (state: State = initState, action: AuthActionType) => {
  switch (action.type) {
    case AuthAction.LOGOUT:
      return { ...action.payload };
    case AuthAction.LOGIN_POSTING:
      return { ...state, posting: true };
    case AuthAction.LOGIN_SUCCESS: // fallthrough
    case AuthAction.LOGIN_FAILURE:
      return { ...state, posting: false, ...(action.payload) };
    default:
      return state;
  }
};
