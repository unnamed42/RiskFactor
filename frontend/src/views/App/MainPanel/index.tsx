import React, { FC } from "react";
import { Link, useLocation } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { Layout, Menu, Icon, Dropdown, Avatar } from "antd";

import { StoreType } from "@/redux";
import { logout } from "@/redux/auth";

import "./index.less";

const UserDropdown: FC = () => {
  const auth = useSelector((state: StoreType) => state.auth);
  const dispatch = useDispatch();

  const dropdown = () => <Menu className="main-user-dropdown">
    <Menu.Item key="1">
      <Icon type="user"/>
      <Link to="/">个人中心</Link>
    </Menu.Item>
    <Menu.Divider/>
    <Menu.Item key="2">
      <Icon type="logout"/>
      <Link to="/login" onClick={() => dispatch(logout())}>退出登录</Link>
    </Menu.Item>
  </Menu>;

  return <Layout.Header className="main-header">
    <div className="main-navbar">
      <Dropdown overlay={dropdown} trigger={["click"]}>
        <span style={{ display: "inline-block" }}>
          <Avatar icon="user" className="main-navbar-avatar"/>
          {auth.token !== null ? auth.username : ""}
          <Icon type="down" className="navbar-icon"/>
        </span>
      </Dropdown>
    </div>
  </Layout.Header>;
};

const Sidebar: FC = () => <Layout.Sider className="main-sider">
  <div className="logo"/>
  <Menu theme="dark" mode="inline" defaultSelectedKeys={["0"]}>
    <Menu.Item key="0">
      <Link to="#">
        <Icon type="database"/>病患数据
      </Link>
    </Menu.Item>
    <Menu.Item key="1">
      <Link to="#">
        <Icon type="area-chart"/>统计分析
      </Link>
    </Menu.Item>
    <Menu.Item key="2">
      <Link to="#">
        <Icon type="import"/>批量导出
      </Link>
    </Menu.Item>
  </Menu>
</Layout.Sider>;

/**
 * 主窗口组件
 */
export const MainPanel: FC = ({ children }) => {
  const { pathname } = useLocation();

  if(pathname === "/")
    return <Layout className="main-panel">
      <UserDropdown/>
      {children}
    </Layout>;

  return <Layout className="main-panel">
    <Sidebar/>
    <Layout>
      <UserDropdown/>
      <Layout.Content className="main-content-container">
        {children}
      </Layout.Content>
    </Layout>
  </Layout>;
};
