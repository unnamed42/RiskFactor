import React, { Suspense, lazy } from "react";
import { render } from "react-dom";
import { HashRouter as Router, Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";

import "@/plugins";

import { persistor, store } from "@/redux";
import { PageLoading, PrivateRoute } from "@/components";
import { PersistGate } from "redux-persist/integration/react";

const Login = lazy(() => import(/* webpackChunkName: 'login' */ "@/views/Login"));
const App   = lazy(() => import(/* webpackChunkName: 'app' */ "@/views/App"));

render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <Router>
        <Suspense fallback={<PageLoading />}>
          <Switch>
            <Route exact path="/login" component={Login} />
            <PrivateRoute path="/" component={App} />
          </Switch>
        </Suspense>
      </Router>
    </PersistGate>
  </Provider>
  , document.getElementById("root"));
