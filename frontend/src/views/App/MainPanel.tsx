import React, { FC } from "react";

import { Layout, Menu } from "antd";
import {
  UserOutlined, DatabaseOutlined,
  AreaChartOutlined, ExportOutlined
} from "@ant-design/icons";

import style from "@/styles/panel.mod.less";
import { UserDropdown } from "@/components";
import { useRouteMatch } from "react-router";
import { Link } from "react-router-dom";

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

const first = [mappings[0].path];

/**
 * 主窗口组件，提供顶栏和侧边菜单，中间部分需要显示其他组件
 */
export const MainPanel: FC = ({ children }) => {
  const { url } = useRouteMatch();

  return <Layout className={style.panel}>
    <Layout.Sider className={style.sider}>
      <div className={style.logo}/>
      <Menu theme="dark" mode="inline" defaultSelectedKeys={first} selectedKeys={[url]}>
        {
          mappings.map(({ path, icon, label }) =>
            <Menu.Item key={path}>
              <Link to={path}>{icon}{label}</Link>
            </Menu.Item>
          )
        }
      </Menu>
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
};
