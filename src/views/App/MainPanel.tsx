import React, { FC } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { Layout, Menu, Icon, Dropdown, Avatar } from "antd";

import { StoreType } from "@/redux";
import { logout } from "@/redux/auth";

import "./MainPanel.less";

/**
 * 主窗口组件
 */
export const MainPanel: FC = ({ children }) => {

  const username = useSelector((state: StoreType) => state.auth.username);
  const dispatch = useDispatch();

  const dropdown = () =>
    <Menu className="main-user-dropdown">
      <Menu.Item key="1">
        <Icon type="user" />
        <Link to="/">个人中心</Link>
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="2">
        <Icon type="logout" />
        <Link to="/login" onClick={() => dispatch(logout())}>退出登录</Link>
      </Menu.Item>
    </Menu>;

  return <Layout hasSider={false} className="main-panel">
    <Layout.Header className="main-header">
      <div className="main-navbar">
        <Dropdown overlay={dropdown} trigger={["click"]}>
          <span>
            <Avatar icon="user" className="main-navbar-avatar" />{username || ""}<Icon type="down" className="navbar-icon" />
          </span>
        </Dropdown>
      </div>
    </Layout.Header>
    {children}
  </Layout>;
};
