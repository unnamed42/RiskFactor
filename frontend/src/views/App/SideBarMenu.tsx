import React, { FC, ReactElement } from "react";
import { Link, useRouteMatch } from "react-router-dom";

import { Menu } from "antd";
import type { MenuProps } from "antd/es/menu";

interface Mapping {
  path: string;
  icon: ReactElement;
  label: string;
}

interface P extends Pick<MenuProps, "theme" | "mode"> {
  mappings: Mapping[];
}

export const SidebarMenu: FC<P> = ({ theme = "dark", mode = "inline", mappings }) => {
  const { url } = useRouteMatch();

  console.log(url);

  const first = [mappings[0].path];

  return <Menu theme={theme} mode={mode} defaultSelectedKeys={first} selectedKeys={[url]}>
    {
      mappings.map(({ path, icon, label }) =>
        <Menu.Item key={path}>
          <Link to={path}>{icon}{label}</Link>
        </Menu.Item>
      )
    }
  </Menu>;
};
