import React, { FC } from "react";

import { Layout } from "antd";
import {
  UserOutlined, DatabaseOutlined,
  AreaChartOutlined, ExportOutlined
} from "@ant-design/icons";

import style from "@/styles/panel.mod.less";
import { SidebarMenu, UserDropdown } from "@/components";

const mappings = [{
  path: "/app/accounts",
  icon: <UserOutlined/>,
  label: "用户信息"
}, {
  path: "/app",
  icon: <DatabaseOutlined/>,
  label: "病患数据"
}, {
  path: "/statistics",
  icon: <AreaChartOutlined/>,
  label: "统计分析"
}, {
  path: "/export",
  icon: <ExportOutlined/>,
  label: "批量导出"
}];

/**
 * 主窗口组件
 */
export const MainPanel: FC = ({ children }) => <Layout className={style.panel}>
  <Layout.Sider className={style.sider}>
    <div className={style.logo}/>
    <SidebarMenu mappings={mappings}/>
  </Layout.Sider>
  <Layout>
    <Layout.Header className={style.header}>
      <div className={style.navbar}>
        <UserDropdown/>
      </div>
    </Layout.Header>
    <Layout.Content className={style.contentContainer}>
      {children}
    </Layout.Content>
  </Layout>
</Layout>;
