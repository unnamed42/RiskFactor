import React, { FC, lazy, Suspense } from "react";
import { Switch, Route, Router } from "react-router";

import { PageLoading } from "@/components";
import { history } from "@/routes";

const Overview = lazy(() => import("./Overview"));

const AppRoutes: FC = props => (
  <Router history={history}>
  <Suspense fallback={PageLoading}>
    <Switch>
        <Route path="overview" component={Overview}/>
    </Switch>
    </Suspense>
    </Router>
);

export default AppRoutes;
