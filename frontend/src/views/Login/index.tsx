import React, { FC } from "react";
import type { Location } from "history";
import { Redirect, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";

import { LoginForm } from "./LoginForm";
import type { StoreType } from "@/redux";

import "./index.less";
import { Demo } from "./demo";

interface Referrer {
  from: Location;
}

export const Login: FC = () => {
  const { state } = useLocation<Referrer>();
  const token = useSelector((store: StoreType) => store.auth.token);

  // 已登录不允许重新进入该界面
  if(token !== null)
    return <Redirect to={state?.from ?? "/"} />;

  return <>
    <div className="login-background"/>
    <div className="login-container">
      <h3 className="login-title">胆囊危险因素分析采集系统</h3>
      <div className="login-wrapper">
        <LoginForm />
      </div>
    </div>
    { process.env.NODE_ENV === "development" ? <Demo/> : null }
  </>;
};

export default Login;
