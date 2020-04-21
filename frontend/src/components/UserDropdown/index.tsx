import React, { FC } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Link } from "react-router-dom";

import { Menu, Dropdown, Avatar } from "antd";
import { UserOutlined, LogoutOutlined, DownOutlined } from "@ant-design/icons";

import type { StoreType } from "@/redux";
import { logout } from "@/redux/auth";

import "./index.less";

const DropdownOverlay: FC = () => {
  const dispatch = useDispatch();

  return <Menu className="rf-dropdown-menu">
    <Menu.Item key="1">
      <Link to="/accounts">
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
};

const inline = {
  display: "inline-block"
};

interface P {
  avatarUrl?: string;
}

export const UserDropdown: FC<P> = ({ avatarUrl }) => {
  const username = useSelector((state: StoreType) => state.auth.lastUsername);

  return <Dropdown overlay={<DropdownOverlay />} trigger={["click"]}>
    <span style={inline}>
      {
        avatarUrl ?
          <Avatar src={avatarUrl} className="rf-avatar" /> :
          <Avatar icon={<UserOutlined />} className="rf-avatar" />
      }
      {username}
      <DownOutlined className="rf-dropdown-icon" />
    </span>
  </Dropdown>;
};
