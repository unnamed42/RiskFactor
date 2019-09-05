import React, { FC, Component } from "react";
import { Route, Redirect, RouteProps } from "react-router";

import { auth } from "@/api/persist";

interface PrivateRouteProps {
    component: Component;
}

const PrivateRoute: FC<RouteProps | PrivateRouteProps> = ({ component, ...rest }) => {
    const isLoggedIn = auth.token !== "";
    return (
        <Route {...rest}
            render={ props => isLoggedIn ?
                (<Component {...props}/>):
                (<Redirect exact to={{ pathname: "/login", state: { from: props.location } }}/>)
            }
        />
    );
};

export default PrivateRoute;
