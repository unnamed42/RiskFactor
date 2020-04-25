import React, { FC } from "react";

import { Layout } from "antd";

import { UserDropdown } from "@/components";
import { SchemaList } from "./SchemaList";

import style from "@/styles/panel.mod.less";

export const Home: FC = () => {
  return <Layout className={style.panel}>
    <Layout.Header className={style.header}>
      <div className={style.navbar}>
        <UserDropdown/>
      </div>
    </Layout.Header>
    <SchemaList />
  </Layout>;
};

export default Home;
