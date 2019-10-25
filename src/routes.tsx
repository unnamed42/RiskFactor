import React, { FC, Suspense, lazy } from "react";
import { HashRouter as Router, Route, Switch } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";

const Login = lazy(() => import(/*webpackChunkName: 'login' */ "@/views/Login"));
const App = lazy(() => import(/* webpackChunkName: 'app' */ "@/views/App"));

export const Routes: FC = () => (
  <Router>
    <Suspense fallback={<PageLoading/>}>
      <Switch>
        <Route exact path="/login" component={Login} />
        <PrivateRoute path="/" component={App} />
      </Switch>
    </Suspense>
  </Router>
);
