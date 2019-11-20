import { combineReducers, createStore, applyMiddleware } from "redux";
import { persistStore, persistReducer } from "redux-persist";
import hardSet from "redux-persist/lib/stateReconciler/hardSet";
import { composeWithDevTools } from "redux-devtools-extension";

import localforage from "localforage";

import { reducer as auth, State as AuthState } from "./auth";
import { reducer as task, State as TaskState } from "./task";
import { PersistConfig } from "redux-persist/es/types";

export interface StoreType {
  auth: AuthState;
  task: TaskState;
}

const config: PersistConfig<StoreType> = {
  key: "root",
  storage: localforage,
  stateReconciler: hardSet
};

const rootReducers = combineReducers({
  auth, task
});

const reducers = persistReducer(config, rootReducers);

export const store = createStore(reducers,
  composeWithDevTools(
    applyMiddleware(
    )
  )
);

export const persistor = persistStore(store);
