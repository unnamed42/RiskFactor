import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute, Loading } from "@/components";

import { AnswerList } from "@/views/Task/AnswerList";
const Accounts = lazy(() => import(/* webpackChunkName: "accounts" */"@/views/Accounts"));
const AnswerForm = lazy(() => import(/* webpackChunkName: "ansform" */"@/views/Task/AnswerForm"));

export interface SchemaRouteParams {
  schemaId: string;
}

export interface FormRouteParams extends SchemaRouteParams {
  answerId?: string;
}

export const Routes: FC = () => <Suspense fallback={<Loading />}>
  <Switch>
    <PrivateRoute path="/accounts" component={Accounts} />
    <PrivateRoute path="/task/:schemaId/answers" component={AnswerList} />
    <PrivateRoute path="/task/:schemaId/form/:answerId?" component={AnswerForm} />
  </Switch>
</Suspense>;
