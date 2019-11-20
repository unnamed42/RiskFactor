import React from "react";
import { withRouter } from "react-router";
import { Redirect } from "react-router-dom";
import { useSelector } from "react-redux";

import { LoginForm } from "./LoginForm";
import { StoreType } from "@/redux";

import "./index.less";

export const Login = withRouter(({ location: { state } }) => {

  const token = useSelector((store: StoreType) => store.auth.token);

  const redirect = <Redirect to={state ? state.from : "/"}/>;

  // 已登录不允许重新进入该界面
  if(token !== null)
    return redirect;

  return <div>
    <div className="login-background"/>
    <div className="login-container">
      <h3 className="login-title">胆囊危险因素分析采集系统</h3>
      <div className="login-wrapper"><LoginForm onLoginSuccess={() => redirect}/></div>
    </div>
  </div>;
});

export default Login;
