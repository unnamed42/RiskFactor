import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";

import { PrivateRoute, Loading } from "@/components";

import { SchemaList } from "@/views/Task/SchemaList";
import { AnswerList } from "@/views/Task/AnswerList";
const Accounts = lazy(() => import(/* webpackChunkName: "accounts" */"@/views/Accounts"));
const AnswerForm = lazy(() => import(/* webpackChunkName: "ansform" */"@/views/Task/AnswerForm"));

import { MainPanel } from "./MainPanel";

export const App: FC = () => <MainPanel>
  <Suspense fallback={<Loading />}>
    <Switch>
      <PrivateRoute exact path="/" component={SchemaList} />
      <PrivateRoute path="/accounts" component={Accounts} />
      <PrivateRoute path="/task/:schemaId/answers" component={AnswerList} />
      <PrivateRoute path="/task/:schemaId/form/:answerId?" component={AnswerForm} />
    </Switch>
  </Suspense>
</MainPanel>;

export default App;
