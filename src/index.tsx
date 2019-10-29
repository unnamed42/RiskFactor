import React, { Suspense, lazy } from "react";
import { render } from "react-dom";
import { HashRouter as Router, Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";

import "@/plugins";

import { store } from "@/redux";
import { PageLoading, PrivateRoute } from "@/components";

const Login = lazy(() => import(/* webpackChunkName: 'login' */ "@/views/Login"));
const Home  = lazy(() => import(/* webpackChunkName: 'home' */ "@/views/Home"));

render(
  <Provider store={store}>
    <Router>
      <Suspense fallback={<PageLoading />}>
        <Switch>
          <Route exact path="/login" component={Login} />
          <PrivateRoute path="/" component={Home} />
        </Switch>
      </Suspense>
    </Router>
  </Provider>
  , document.getElementById("root"));
