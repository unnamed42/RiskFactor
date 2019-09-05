import React, { FC, Suspense, lazy } from "react";
import { Router, Route, Switch } from "react-router-dom";
import { createHashHistory } from "history";

import PrivateRoute from "./PrivateRoute";
import { PageLoading } from "@/components";

const Login = lazy(() => import("@/views/Login"));
const Home =  lazy(() => import("@/views/App"));

const history = createHashHistory();

const Routes: FC = () => (
  <Router history={history}>
    <Suspense fallback={<PageLoading/>}>
      <Switch>
        <Route exact path="/login" component={Login} />
        <PrivateRoute exact path="/" component={Home} />
      </Switch>
    </Suspense>
  </Router>
);

export { Routes, history };
