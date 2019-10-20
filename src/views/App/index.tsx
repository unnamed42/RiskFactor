import React, { useState } from "react";
import { withRouter } from "react-router";
import { Link } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";

import { Layout, Menu, Icon, Dropdown, Avatar } from "antd";

import { StoreType } from "@/redux";
import { logout } from "@/redux/auth";
import AppRoutes from "./AppRoutes";

import "./index.less";

const App = withRouter(({ location, ...props }) => {
  const [collapsed, setCollapsed] = useState(false);
  const dispatch = useDispatch();

  const username = useSelector((state: StoreType) => state.auth.username);

  const toggle = () => setCollapsed(!collapsed);

  const userDropdown = (
    <Menu className="main-user-dropdown">
      <Menu.Item key="1">
        <Icon type="user" />
        <Link to="/">个人中心</Link>
      </Menu.Item>
      <Menu.Divider/>
      <Menu.Item key="2">
        <Icon type="logout" />
        <Link to="/login" onClick={() => dispatch(logout())}>退出登录</Link>
      </Menu.Item>
    </Menu>
  );

  return (
    <Layout className="main-panel">

      <Layout.Sider trigger={null} collapsible collapsed={collapsed} className="main-sider">
        <div className="logo" />
        <Menu theme="dark" mode="inline" defaultSelectedKeys={[location.pathname]}>
          <Menu.Item key="/overview">
            <Link to="/overview">
              <Icon type="dashboard" />
              <span>概览</span>
            </Link>
          </Menu.Item>
          <Menu.Item key="/forms">
            <Link to="/forms">
              <Icon type="form" />
              <span>表格</span>
            </Link>
          </Menu.Item>
        </Menu>
      </Layout.Sider>

      <Layout>
        <Layout.Header style={{ background: "#fff", padding: 0 }}>
          <Icon className="trigger" type={collapsed ? "menu-unfold" : "menu-fold"} onClick={toggle} />
          <div className="main-navbar">
            <Dropdown overlay={userDropdown} trigger={["click"]}>
              <span><Avatar icon="user" /> { username || "" }<Icon type="down" className="navbar-icon"/></span>
            </Dropdown>
          </div>
        </Layout.Header>
        <Layout.Content style={{ margin: "24px 16px", padding: 24, background: "#fff", minHeight: 280 }}>
          <AppRoutes/>
        </Layout.Content>
      </Layout>

    </Layout>
  );
});

export default App;
// import forms from "./views/Forms";
// export default forms;
