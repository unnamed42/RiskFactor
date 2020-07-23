import React, { FC } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Link } from "react-router-dom";

import { Menu, Dropdown, Avatar } from "antd";
import { UserOutlined, LogoutOutlined, DownOutlined } from "@ant-design/icons";

import type { StoreType } from "@/redux";
import { logout } from "@/redux/auth";

import style from "@/styles/dropdown.mod.less";

interface P {
  avatarUrl?: string;
}

/**
 * 圆角用户头像和旁边小三角按钮和单击弹出菜单
 * @param avatarUrl 头像的URL,可以不填，不填以默认头像代替
 */
export const UserDropdown: FC<P> = ({ avatarUrl }) => {
  const auth = useSelector((state: StoreType) => state.auth);
  const dispatch = useDispatch();

  const menu = <Menu className={style.dropdown}>
    <Menu.Item key="1">
      <Link to="/app/accounts">
        <UserOutlined />用户信息
      </Link>
    </Menu.Item>
    <Menu.Divider />
    <Menu.Item key="2">
      <Link to="/login" onClick={() => dispatch(logout())}>
        <LogoutOutlined />退出登录
      </Link>
    </Menu.Item>
  </Menu>;

  return <Dropdown overlay={menu} trigger={["click"]}>
    <span className={style.inline}>
      {
        avatarUrl ?
          // className="rf-avatar"
          <Avatar src={avatarUrl} /> :
          <Avatar icon={<UserOutlined />} />
      }
      {auth.token ? auth.username : null}
      <DownOutlined className={style.dropdownIcon} />
    </span>
  </Dropdown>;
};
