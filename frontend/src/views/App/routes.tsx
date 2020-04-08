import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute, Loading } from "@/components";

const AnswerForm = lazy(() => import(/* webpackChunkName: "answer-form" */ "@/views/Task/AnswerForm"));
const Accounts = lazy(() => import(/* webpackChunkName: "accounts" */ "@/views/Accounts"));
const SchemaList = lazy(() => import(/* webpackChunkName: "schema-list" */"@/views/Task/SchemaList"));
const AnswerList = lazy(() => import(/* webpackChunkName: "answer-list" */"@/views/Task/AnswerList"));

export const Routes: FC = () => <Suspense fallback={<Loading/>}>
  <Switch>
    <PrivateRoute exact path="/">
      <SchemaList/>
    </PrivateRoute>
    <PrivateRoute path="/accounts">
      <Accounts/>
    </PrivateRoute>
    <PrivateRoute path="/task/:schemaId/answers">
      <AnswerList/>
    </PrivateRoute>
    <PrivateRoute path="/task/:schemaId/form/:answerId?">
      <AnswerForm/>
    </PrivateRoute>
  </Switch>
</Suspense>;
