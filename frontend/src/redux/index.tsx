import { combineReducers, createStore, applyMiddleware } from "redux";
import { persistStore, persistReducer } from "redux-persist";
import hardSet from "redux-persist/lib/stateReconciler/hardSet";
import { composeWithDevTools } from "redux-devtools-extension/developmentOnly";

import localforage from "localforage";

import { reducer as auth, AuthState } from "./auth";
import { reducer as cache, CacheState } from "./cache";
import type { PersistConfig } from "redux-persist/es/types";

export interface StoreType {
  auth: AuthState;
  cache: CacheState;
}

const config: PersistConfig<StoreType> = {
  key: "root",
  storage: localforage,
  stateReconciler: hardSet
};

const rootReducers = combineReducers({
  auth, cache
});

const reducers = persistReducer(config, rootReducers);

export const store = createStore(reducers,
  composeWithDevTools(
    applyMiddleware(
    )
  )
);

export const persistor = persistStore(store);
