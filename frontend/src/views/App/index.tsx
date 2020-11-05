import React, { FC, lazy, Suspense } from "react";
import { Switch } from "react-router-dom";
import { useRouteMatch } from "react-router";

import { PrivateRoute, Loading } from "@/components";

import { AnswerList } from "@/views/Task/AnswerList";
const Accounts = lazy(() => import("@/views/Accounts"));
const AnswerForm = lazy(() => import("@/views/Task/AnswerForm"));

import { MainPanel } from "./MainPanel";

/**
 * 应用的主页面，问卷填写等等功能都是经由主页面调用不同组件来展示
 */
export const App: FC = () => {
  const { url } = useRouteMatch();
  return <MainPanel>
    <Suspense fallback={<Loading/>}>
      <Switch>
        <PrivateRoute path={`${url}/accounts`} component={Accounts}/>
        <PrivateRoute path={`${url}/:schemaId/answers`} component={AnswerList}/>
        <PrivateRoute path={`${url}/:schemaId/form/:answerId?`} component={AnswerForm}/>
      </Switch>
    </Suspense>
  </MainPanel>;
};

export default App;
