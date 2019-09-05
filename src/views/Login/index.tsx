import React from "react";
import { withRouter } from "react-router";

import { LoginForm } from "@/components";

import "./index.less";

const Login = withRouter(({ history, ...props }) => {

  const redirect = () => {
    const { state } = props.location;
    const referrer = state ? state.from : "/";
    history.push(referrer);
  };

  return (
    <div>
      <div className="login-background" />
      <div className="login-container">
        <h3 className="login-title">胆囊危险因素分析采集系统</h3>
        <div className="login-wrapper"><LoginForm onLoginSuccess={redirect} /></div>
      </div>
    </div>
  );
});

export default Login;
