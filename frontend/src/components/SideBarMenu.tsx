import React, { FC } from "react";
import { Link, useLocation } from "react-router-dom";

import { Menu } from "antd";
import type { MenuProps } from "antd/es/menu";
import {
  UserOutlined, DatabaseOutlined,
  AreaChartOutlined, ExportOutlined
} from "@ant-design/icons";

type P = Pick<MenuProps, "theme" | "mode">;

const mappings = [{
  path: "/accounts",
  icon: UserOutlined,
  label: "用户信息"
}, {
  path: "/",
  icon: DatabaseOutlined,
  label: "病患数据"
}, {
  path: "/statistics",
  icon: AreaChartOutlined,
  label: "统计分析"
}, {
  path: "/export",
  icon: ExportOutlined,
  label: "批量导出"
}];

export const SidebarMenu: FC<P> = ({ theme = "dark", mode = "inline" }) => {
  const { pathname } = useLocation();

  const here = `/${pathname.split(/[\\/?]/)[1]}`;

  return <Menu theme={theme} mode={mode} defaultSelectedKeys={["/"]} selectedKeys={[here]}>
    {
      mappings.map(({ path, icon: Icon, label }) =>
        <Menu.Item key={path}>
          <Link to={path}><Icon />{label}</Link>
        </Menu.Item>
      )
    }
  </Menu>;
};
