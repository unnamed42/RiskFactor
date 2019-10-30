import React, { FC } from "react";

import { MainPanel } from "./MainPanel";
import { Routes } from "./routes";

export const App: FC = () =>
  <MainPanel>
    <Routes/>
  </MainPanel>;

export default App;
