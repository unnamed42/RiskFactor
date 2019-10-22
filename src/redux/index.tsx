import { combineReducers, createStore, applyMiddleware } from "redux";
import thunkMiddleware from "redux-thunk";
import { composeWithDevTools } from "redux-devtools-extension";

import { reducer as auth } from "./auth";

const reducers = combineReducers({
  auth
});

export const store = createStore(reducers,
  composeWithDevTools(
    applyMiddleware(
      thunkMiddleware
    )
  )
);

export type StoreType = ReturnType<typeof store.getState>;
