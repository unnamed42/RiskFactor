import React, { FC, useState } from "react";
import { Link } from "react-router-dom";
import { useSelector, useDispatch, connect } from "react-redux";

import { Layout, Menu, Icon, Dropdown, Avatar, Card } from "antd";

import { StoreType } from "@/redux";
import { logout } from "@/redux/auth";

import "./index.less";

const App: FC = props => {
  const [collapsed, setCollapsed] = useState(false);
  const dispatch = useDispatch();

  const username = useSelector((state: StoreType) => state.auth.username);

  const toggle = () => setCollapsed(!collapsed);

  const userDropdown = (
    <Menu className="main-user-dropdown">
      <Menu.Item key="1">
        <Icon type="user" />
        <Link to="">个人中心</Link>
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

      <Layout.Sider trigger={null} collapsible collapsed={collapsed}>
        <div className="logo" />
        <Menu theme="dark" mode="inline" defaultSelectedKeys={["1"]}>
          <Menu.Item key="1">
            <Icon type="dashboard" />
            <span>概览</span>
          </Menu.Item>
          <Menu.Item key="2">
            <Icon type="form" />
            <span>表格</span>
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
          <Card title="已有表单" bordered={false}/>
        </Layout.Content>
      </Layout>

    </Layout>
  );
};

export default App;
