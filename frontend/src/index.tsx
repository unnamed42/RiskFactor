import React, { Suspense, lazy } from "react";
import { render } from "react-dom";
import { HashRouter as Router, Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";

import "@/plugins";

import { persistor, store } from "@/redux";
import { Loading, PrivateRoute, ErrorBoundary } from "@/components";

const Login = lazy(() => import("@/views/Login"));
const Home = lazy(() => import("@/views/Home"));
const App = lazy(() => import("@/views/App"));

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
