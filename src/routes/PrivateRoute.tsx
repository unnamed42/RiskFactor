import React, { FC, Component } from "react";
import { Route, Redirect, RouteProps } from "react-router";

import { auth } from "@/api/persist";

interface PrivateRouteProps {
    component: Component;
    redirect: string;
}

const PrivateRoute: FC<RouteProps | PrivateRouteProps> = ({ component, redirect, ...rest }) => {
    const isLoggedIn = auth.token !== "";
    return (
        <Route {...rest}
            render={ props => isLoggedIn ?
                (<Component {...props}/>):
                (<Redirect to={{ pathname: redirect, state: { from: props.location } }}/>)
            }
        />
    );
};

export default PrivateRoute;
