import React, { Suspense, lazy } from "react";
import { render } from "react-dom";
import { HashRouter as Router, Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";

import "@/plugins";

import { persistor, store } from "@/redux";
import { Loading, PrivateRoute, ErrorBoundary } from "@/components";

const Login = lazy(() => import(/* webpackChunkName: "login" */"@/views/Login"));
const Home = lazy(() => import(/* webpackChunkName: "home" */"@/views/Home"));
const App = lazy(() => import(/* webpackChunkName: "app" */"@/views/App"));

const Routes = () => <Router>
  <Suspense fallback={<Loading />}>
    <Switch>
      <Route exact path="/login" component={Login} />
      <PrivateRoute exact path="/" component={Home} />
      <PrivateRoute path="/app" component={App} />
    </Switch>
  </Suspense>
</Router>;

render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <ErrorBoundary>
        <Routes/>
      </ErrorBoundary>
    </PersistGate>
  </Provider>
  , document.getElementById("root"));
