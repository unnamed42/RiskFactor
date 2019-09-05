import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";

import "@/plugins";

import { Routes } from "@/routes";
import { store } from "@/redux";

render(
  <Provider store={store}>
    <Routes/>
  </Provider>
  , document.getElementById("root"));
