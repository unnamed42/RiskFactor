import React, { FC, Suspense, lazy } from "react";
import { HashRouter as Router, Route, Switch } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";

const Login = lazy(() => import("@/views/Login"));
const Home =  lazy(() => import("@/views/App"));

export const Routes: FC = () => (
  <Router>
    <Suspense fallback={<PageLoading/>}>
      <Switch>
        <Route exact path="/login" component={Login} />
        <PrivateRoute path="/" component={Home} />
      </Switch>
    </Suspense>
  </Router>
);
