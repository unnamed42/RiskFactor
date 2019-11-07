import React, { FC } from "react";

import { Spin } from "antd";

/**
 * 当组件加载数据时，显示Loading图标作为暂时替代
 */
export const PageLoading: FC = () => (
  <div style={{ paddingTop: 100, textAlign: "center" }}>
    <Spin size="large"/>
  </div>
);
