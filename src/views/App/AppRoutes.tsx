import React, { FC, lazy, Suspense } from "react";
import { Route, Redirect } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";

const Overview = lazy(() => import("./views/Overview"));
const Forms = lazy(() => import("./views/Forms"));
const Success = lazy(() => import("./views/Success"));

const AppRoutes: FC = () => (
  <Suspense fallback={<PageLoading />}>
    <Route exact path="/" component={() => (<Redirect to="/overview" />)} />
    <PrivateRoute path="/overview" component={Overview} />
    <PrivateRoute path="/forms" component={Forms} />
    <PrivateRoute path="/success" component={Success} />
  </Suspense>
);

export default AppRoutes;
