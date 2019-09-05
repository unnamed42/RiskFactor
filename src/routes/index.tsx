import React, { FC, Suspense, lazy } from "react";
import { HashRouter, Route, Switch } from "react-router-dom";

import PrivateRoute from "./PrivateRoute";
import { PageLoading } from "@/components";

const Login = lazy(() => import("@/views/Login"));
const Home =  lazy(() => import("@/views/App"));

const Routes: FC = () => (
  <HashRouter>
    <Suspense fallback={<PageLoading/>}>
      <Switch>
        <Route exact path="/login" component={Login} />
        <PrivateRoute exact path="/" component={Home} />
      </Switch>
    </Suspense>
  </HashRouter>
);

export default Routes;
