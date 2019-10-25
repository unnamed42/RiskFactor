import React, { FC, Suspense, lazy } from "react";
import { Route, Redirect } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";

const Overview = lazy(() => import(/*webpackChunkName: 'overview' */ "./views/Overview"));
const Forms = lazy(() => import(/*webpackChunkName: 'forms' */ "./views/Forms"));
const Success = lazy(() => import(/*webpackChunkName: 'success' */ "./views/Success"));

export const AppRoutes: FC = () => (
  <Suspense fallback={<PageLoading />}>
    <Route exact path="/" component={() => (<Redirect to="/overview" />)} />
    <PrivateRoute path="/overview" component={Overview} />
    <PrivateRoute path="/forms" component={Forms} />
    <PrivateRoute path="/success" component={Success} />
  </Suspense>
);
