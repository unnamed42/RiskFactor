import React, { FC } from "react";
import { Redirect, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";
import type { Location } from "history";

import { LoginForm } from "./LoginForm";
import type { StoreType } from "@/redux";

import style from "@/styles/login-form.mod.less";
import { Demo } from "./demo";

interface Referrer {
  from: Location;
}

export const Login: FC = () => {
  const { state } = useLocation<Referrer | undefined>();
  const token = useSelector((store: StoreType) => store.auth.token);

  // 已登录不允许重新进入该界面
  if(token !== null)
    return <Redirect to={state?.from ?? "/"} />;

  return <>
    <div className={style.background}/>
    <div className={style.container}>
      <h3 className={style.title}>胆囊危险因素分析采集系统</h3>
      <div className={style.wrapper}>
        <LoginForm />
      </div>
    </div>
    {/* 测试效果用的组件，仅在development模式出现 */}
    { process.env.NODE_ENV === "development" ? <Demo/> : null }
  </>;
};

export default Login;
