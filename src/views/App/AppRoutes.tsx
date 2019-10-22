import React, { FC, lazy, Suspense } from "react";
import { Route, Redirect } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";

const Forms = lazy(() => import("./views/Forms"));
import { Overview } from "./views/Overview";
import { Success } from "./views/Success";

export const AppRoutes: FC = () => (
  <Suspense fallback={<PageLoading />}>
    <Route exact path="/" component={() => (<Redirect to="/overview" />)} />
    <PrivateRoute path="/overview" component={Overview} />
    <PrivateRoute path="/forms" component={Forms} />
    <PrivateRoute path="/success" component={Success} />
  </Suspense>
);
