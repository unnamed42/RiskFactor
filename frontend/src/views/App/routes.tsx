import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute, Loading } from "@/components";

const AnswerForm = lazy(() => import(/* webpackChunkName: "answer-form" */ "@/views/Task/AnswerForm"));
const Accounts = lazy(() => import(/* webpackChunkName: "accounts" */ "@/views/Accounts"));
const SchemaList = lazy(() => import(/* webpackChunkName: "schema-list" */"@/views/Task/SchemaList"));
const AnswerList = lazy(() => import(/* webpackChunkName: "answer-list" */"@/views/Task/AnswerList"));

export const Routes: FC = () => <Suspense fallback={<Loading/>}>
  <Switch>
    <PrivateRoute exact path="/" component={SchemaList}/>
    <PrivateRoute path="/accounts" component={Accounts}/>
    <PrivateRoute path="/task/:schemaId/answers" component={AnswerList}/>
    <PrivateRoute path="/task/:schemaId/form/:answerId?" component={AnswerForm}/>
  </Switch>
</Suspense>;
