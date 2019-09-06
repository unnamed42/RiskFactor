import React, { FC } from "react";
import { Route, Redirect, RouteProps } from "react-router";
import { useSelector } from "react-redux";

import { StoreType } from "@/redux";

const PrivateRoute: FC<RouteProps> = ({ component, ...rest }) => {
  const token = useSelector((state: StoreType) => state.auth.token);
  const Component = component!;
  return (
    <Route {...rest}
      render={ props => token !== "" ?
        (<Component {...props}/>):
        (<Redirect exact to={{ pathname: "/login", state: { from: props.location } }}/>)
      }
    />
  );
};

export default PrivateRoute;
