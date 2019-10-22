import React, { FC } from "react";

import { Spin } from "antd";

export const PageLoading: FC = () => (
  <div style={{ paddingTop: 100, textAlign: "center" }}>
    <Spin size="large"/>
  </div>
);
