import React, { FC } from "react";
import { Route, Redirect, RouteProps } from "react-router";

import { store } from "@/redux";

/**
 * 自定义`Route`，当未登录时自动跳转回登录页面
 *
 * 目前登录状态的检查通过查看本地存储的`token`是否为`null`来完成
 */
export const PrivateRoute: FC<RouteProps> = ({ children, ...props }) => {
  return <Route {...props}>
    {
      store.getState().auth.token === null ?
        <Redirect exact to={{ pathname: "/login", state: { from: props.location } }} /> :
        children
    }
  </Route>;
};
