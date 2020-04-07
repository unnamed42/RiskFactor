import React, { FC, CSSProperties } from "react";

import { Spin } from "antd";

const style: CSSProperties = {
  paddingTop: 100,
  textAlign: "center"
};

/**
 * 当组件加载数据时，显示Loading图标作为暂时替代
 */
export const Loading: FC = () => <div style={style}>
  <Spin size="large" />
</div>;
