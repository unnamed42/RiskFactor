import React, { FC } from "react";
import { Redirect, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";

import { LoginForm } from "./LoginForm";
import { StoreType } from "@/redux";

import "./index.less";

export const Login: FC = () => {
  const token = useSelector((store: StoreType) => store.auth.token);
  const { state } = useLocation();

  const redirect = <Redirect to={state ? state.from : "/"} />;

  // 已登录不允许重新进入该界面
  if(token !== null)
    return redirect;

  return <>
    <div className="login-background"/>
    <div className="login-container">
      <h3 className="login-title">胆囊危险因素分析采集系统</h3>
      <div className="login-wrapper"><LoginForm onLoginSuccess={() => redirect}/></div>
    </div>
  </>;
};

export default Login;
