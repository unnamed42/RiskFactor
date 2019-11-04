import React, { FC } from "react";
import { Route, Redirect, RouteProps } from "react-router";
import { useSelector } from "react-redux";

import { StoreType } from "@/redux";

export const PrivateRoute: FC<RouteProps> = ({ component: Component, render, ...rest }) => {
  const token = useSelector((state: StoreType) => state.auth.token);
  return (
    <Route {...rest}
      render={ props => token ?
        (Component && <Component {...props}/>) || (render && render(props)) :
        (<Redirect exact to={{ pathname: "/login", state: { from: props.location } }}/>)
      }
    />
  );
};
