import React, { FC } from "react";
import { BrowserRouter, Route } from "react-router-dom";

import loadable from "@loadable/component";
import PrivateRoute from './PrivateRoute';

const Loading: FC = () => (<p>加载中……</p>);

const Login = loadable(() => import("@/views/Login"), {
  fallback: <Loading />
});

const Routes: FC = () => (
  <BrowserRouter>
    <PrivateRoute path="/"/>
    <Route path="/login" component={ (props: any) => <Login {...props}/> }/>
  </BrowserRouter>
);

export default Routes;
