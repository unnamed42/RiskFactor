import React from "react";
import { withRouter } from "react-router";

import { LoginForm } from "./LoginForm";

import "./index.less";
import { useSelector } from "react-redux";
import { StoreType } from "@/redux";

export const Login = withRouter(({ history, location: { state } }) => {

  const token = useSelector((store: StoreType) => store.auth.token);

  const redirect = () =>
    history.push(state ? state.from : "/");

  // 已登录不允许重新进入该界面
  if(token !== null)
    redirect();

  return <div>
    <div className="login-background"/>
    <div className="login-container">
      <h3 className="login-title">胆囊危险因素分析采集系统</h3>
      <div className="login-wrapper"><LoginForm onLoginSuccess={redirect}/></div>
    </div>
  </div>;
});

export default Login;
