import React, { FC } from "react";
import { Link, useLocation } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { Layout, Menu, Dropdown, Avatar } from "antd";
import {
  UserOutlined, LogoutOutlined, DatabaseOutlined,
  DownOutlined, AreaChartOutlined, ImportOutlined
} from "@ant-design/icons";

import type { StoreType } from "@/redux";
import { logout } from "@/redux/auth";

import "./index.less";

const UserDropdown: FC = () => {
  const auth = useSelector((state: StoreType) => state.auth);
  const dispatch = useDispatch();

  const Overlay = () => <Menu className="main-user-dropdown">
    <Menu.Item key="1">
      <UserOutlined/>
      <Link to="/accounts">用户信息</Link>
    </Menu.Item>
    <Menu.Divider/>
    <Menu.Item key="2">
      <LogoutOutlined/>
      <Link to="/login" onClick={() => dispatch(logout())}>退出登录</Link>
    </Menu.Item>
  </Menu>;

  return <Dropdown overlay={Overlay} trigger={["click"]}>
    <span style={{ display: "inline-block" }}>
      <Avatar icon={<UserOutlined />} className="main-navbar-avatar" />
      {auth.token !== null ? auth.username : ""}
      <DownOutlined className="navbar-icon" />
    </span>
  </Dropdown>;
};

const SidebarMenu: FC = () => <Menu theme="dark" mode="inline" defaultSelectedKeys={["1"]}>
  <Menu.Item key="0">
    <Link to="/accounts">
      <UserOutlined />用户信息
    </Link>
  </Menu.Item>
  <Menu.Item key="1">
    <Link to="/">
      <DatabaseOutlined />病患数据
    </Link>
  </Menu.Item>
  <Menu.Item key="2">
    <Link to="#">
      <AreaChartOutlined />统计分析
    </Link>
  </Menu.Item>
  <Menu.Item key="3">
    <Link to="#">
      <ImportOutlined />批量导出
    </Link>
  </Menu.Item>
</Menu>;

/**
 * 主窗口组件
 */
export const MainPanel: FC = ({ children }) => {
  const { pathname } = useLocation();

  const dropdown = <Layout.Header className="main-header">
    <div className="main-navbar">
      <UserDropdown />
    </div>
  </Layout.Header>;

  if(pathname === "/")
    return <Layout className="main-panel">
      {dropdown}
      {children}
    </Layout>;

  return <Layout className="main-panel">
    <Layout.Sider className="main-sider">
      <div className="logo" />
      <SidebarMenu />
    </Layout.Sider>
    <Layout>
      {dropdown}
      <Layout.Content className="main-content-container">
        {children}
      </Layout.Content>
    </Layout>
  </Layout>;
};
