import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router";

import { PageLoading, PrivateRoute } from "@/components";

const Overview = lazy(() => import("./views/Overview"));
const Forms = lazy(() => import("./views/Forms"));
const Success = lazy(() => import("./views/Success"));

const AppRoutes: FC = () => (
  <Suspense fallback={<PageLoading/>}>
    <Switch>
      <PrivateRoute path="/" component={Overview} />
      <PrivateRoute path="/forms" component={Forms} />
      <PrivateRoute path="/success" component={Success} />
    </Switch>
  </Suspense>
);

export default AppRoutes;
