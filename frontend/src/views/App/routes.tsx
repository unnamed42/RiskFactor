import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute, Loading } from "@/components";

import { SchemaList } from "@/views/Task/SchemaList";
import { AnswerList } from "@/views/Task/AnswerList";

const AnswerForm = lazy(() => import(/* webpackChunkName: "answer-form" */ "@/views/Task/AnswerForm"));

export const Routes: FC = () => <Suspense fallback={<Loading/>}>
  <Switch>
    <PrivateRoute exact path="/">
      <SchemaList/>
    </PrivateRoute>
    <PrivateRoute path="/task/:schemaId/answers">
      <AnswerList/>
    </PrivateRoute>
    <PrivateRoute path="/task/:schemaId/form">
      <AnswerForm/>
    </PrivateRoute>
    <PrivateRoute path="/task/:schemaId/form/:answerId">
      <AnswerForm/>
    </PrivateRoute>
  </Switch>
</Suspense>;
