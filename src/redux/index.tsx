import { combineReducers, createStore, applyMiddleware } from "redux";
import thunkMiddleware from "redux-thunk";

import { reducer as auth } from "./auth";

const reducers = combineReducers({
  auth
});

const store = createStore(reducers,
  applyMiddleware(
    thunkMiddleware
  )
);

type StoreType = ReturnType<typeof store.getState>;

export { store, StoreType };
