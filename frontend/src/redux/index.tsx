import { combineReducers, createStore, applyMiddleware } from "redux";
import thunkMiddleware from "redux-thunk";
import { composeWithDevTools } from "redux-devtools-extension";

import { reducer as auth, State as AuthState } from "./auth";
import { reducer as task, State as TaskState } from "./task";

const reducers = combineReducers({
  auth, task
});

export const store = createStore(reducers,
  composeWithDevTools(
    applyMiddleware(
      thunkMiddleware
    )
  )
);

export interface StoreType {
  auth: AuthState;
  task: TaskState;
}
