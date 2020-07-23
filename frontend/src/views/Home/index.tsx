import React, { FC } from "react";

import { Layout } from "antd";

import { UserDropdown } from "@/components";
import { SchemaList } from "./SchemaList";

import style from "@/styles/panel.mod.less";

/**
 * 登录后跳转的页面，但并非主要页面。该页面展示所有的“项目”（即问卷），点击项目之后才是
 * 应用的具体界面
 */
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
