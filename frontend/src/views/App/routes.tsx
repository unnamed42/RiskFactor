import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute, PageLoading } from "@/components";

import { TaskList } from "@/views/Task/TaskList";
import { AnswerList } from "@/views/Task/AnswerList";

const AnswerForm = lazy(() => import(/* webpackChunkName: "answer-form" */ "@/views/Task/AnswerForm"));

export const Routes: FC = () =>
  <Suspense fallback={<PageLoading />}>
    <Switch>
      <PrivateRoute exact path="/" component={TaskList} />
      <PrivateRoute path="/task/:id/answers"
        render={({ match }) => <AnswerList taskId={match.params.id} />} />
      <PrivateRoute path="/task/:id/form/:ansId"
        render={({ match: { params } }) => <AnswerForm taskId={params.id} answerId={params.ansId} /> } />

      <PrivateRoute path="/task/:id/form"
        render={({ match: { params } }) => <AnswerForm taskId={params.id} /> } />
    </Switch>
  </Suspense>;
