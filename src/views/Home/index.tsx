import React, { FC } from "react";

import { MainPanel } from "@/components";
import { Routes } from "./routes";

export const Home: FC = () =>
  <MainPanel>
    <Routes/>
  </MainPanel>;

export default Home;
