import React, { FC } from "react";

import { Layout } from "antd";

import { UserDropdown } from "@/components";
import SchemaList from "./SchemaList";

export const Home: FC = () => {
  return <Layout className="rf-main">
    <Layout.Header className="rf-main-header">
      <div className="rf-main-navbar">
        <UserDropdown/>
      </div>
    </Layout.Header>
    <SchemaList />
  </Layout>;
};

export default Home;
