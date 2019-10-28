import { Action } from "redux";
import { ThunkAction } from "redux-thunk";

import * as api from "@/api/login";
import { auth } from "@/api/persist";

const storageProps = () => ({
  username: auth.username,
  token: auth.token,
  expiry: auth.expiry
});

///
/// types
///
enum AuthAction {
  AUTH_INFO = "auth/info",
  LOGOUT = "auth/logout",
  // types for async loading
  LOGIN_POSTING = "auth/login-posting",
  LOGIN_SUCCESS = "auth/login-success",
  LOGIN_FAILURE = "auth/login-failure",
}

const initState = {
  ...storageProps(),

  posting: false,
  error: null as string | null
};

///
/// actions
///
interface LoginPayload extends api.LoginPayload {
  remember: boolean;
}
interface AuthActionType {
  type: AuthAction;
  payload?: Readonly<{ token: string; username: string; expiry: number; }> | Readonly<{ error: string; }>;
}

type AuthActionThunk = ThunkAction<void, typeof initState, null, Action<AuthAction>>;

export const login = (payload: LoginPayload, onLoginSuccess?: () => void): AuthActionThunk => async dispatch => {
  dispatch({ type: AuthAction.LOGIN_POSTING });
  try {
    const { token } = await api.login(payload);
    // this is needed for http requests
    auth.token = token;
    const { username, expire_at } = await api.tokenInfo();
    auth.username = username;
    auth.expiry = Number(expire_at);
    dispatch({ type: AuthAction.LOGIN_SUCCESS, payload: { token, username, expiry: expire_at } });
    if (onLoginSuccess) onLoginSuccess();
  } catch (e) {
    const error = e as ApiError;
    dispatch({ type: AuthAction.LOGIN_FAILURE, payload: { error: error.message } });
  }
};

export const logout = (): AuthActionType => {
  auth.clear();
  return { type: AuthAction.LOGOUT, payload: { username: "", token: "", expiry: -1 } };
};

///
/// reducers
///
export const reducer = (state = initState, action: AuthActionType) => {
  switch (action.type) {
    case AuthAction.LOGOUT:
      return { ...state, ...(action.payload) };
    case AuthAction.LOGIN_POSTING:
      return { ...state, posting: true };
    case AuthAction.LOGIN_SUCCESS: // fallthrough
    case AuthAction.LOGIN_FAILURE:
      return { ...state, posting: false, ...(action.payload) };
    default: return state;
  }
};
