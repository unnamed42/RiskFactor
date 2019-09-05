import React, { FC, useState } from "react";

import { Layout, Menu, Icon } from "antd";

import "./index.less";

const App: FC = () => {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <Layout className="main-layout">
      <Layout.Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div className="logo" />
        <Menu theme="dark" defaultSelectedKeys={["1"]} mode="inline">
          <Menu.Item key="1">
            <Icon type="pie-chart" />
            <span>Option 1</span>
          </Menu.Item>

          <Menu.Item key="2">
              <Icon type="desktop" />
              <span>Option 2</span>
          </Menu.Item>

          <Menu.SubMenu key="sub1" title={
            <span><Icon type="user"/><span>User</span></span>
          }>
            <Menu.Item key="3">Tom</Menu.Item>
            <Menu.Item key="4">Bill</Menu.Item>
            <Menu.Item key="5">Alex</Menu.Item>
          </Menu.SubMenu>
        </Menu>
      </Layout.Sider>

      <Layout>
        <Layout.Header className="main-header"/>
        <Layout.Content>
          <div className="main-content">content</div>
        </Layout.Content>
        <Layout.Footer className="main-footer">
          TJH Dannang RiskFactor
        </Layout.Footer>
      </Layout>
    </Layout>
  );
};

export default App;
