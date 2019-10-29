import React, { FC, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";
import { Tasks } from "./Tasks";

export const Routes: FC = () =>
  <Suspense fallback={<PageLoading />}>
    <Switch>
      <PrivateRoute exact path="/" component={Tasks} />
      <PrivateRoute path="/task/:id" render={({ match }) => <span>{match.params.id}</span>} />
    </Switch>
  </Suspense>;
