import React, { FC, Suspense, lazy } from "react";
import { Switch } from "react-router-dom";

import { PageLoading, PrivateRoute } from "@/components";
import { TaskList } from "./TaskList";
const Task = lazy(() => import(/*webpackChunkName: 'task-answers' */ "./Task"));

export const Routes: FC = () =>
  <Suspense fallback={<PageLoading />}>
    <Switch>
      <PrivateRoute exact path="/" component={TaskList} />
      <PrivateRoute path="/task/:id"
        render={({ match }) => <Task taskId={match.params.id} />} />
    </Switch>
  </Suspense>;
