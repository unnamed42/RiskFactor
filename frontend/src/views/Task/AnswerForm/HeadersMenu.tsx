import React, { FC, CSSProperties, useEffect } from "react";

import { Menu } from "antd";
import { BarsOutlined } from "@ant-design/icons";

const menuStyle: CSSProperties = { height: "100%", borderRight: 0 };

interface HeadersMenuProps {
  headers: Record<string, any>;
  onChange?: (value: string) => void;
}

const renderItems = (headers: any, parent = "") => Object.entries(headers).map(([k, v]) => {
  const itemKey = parent ? `${parent}/${k}` : k;
  if (v === null)
    return <Menu.Item key={itemKey}>{k}</Menu.Item>
  return <Menu.SubMenu key={itemKey} title={<span><BarsOutlined />{k}</span>}>
    {renderItems(v, itemKey)}
  </Menu.SubMenu>
});

export const openKey = (obj: any): string[] => {
  const path: string[] = [];
  // 自headers层级结构找到第一个叶子节点
  while (obj !== null) {
    const entries = Object.entries(obj);
    if (entries.length === 0)
      break;
    const [k, v] = entries[0];
    path.push(k);
    if (v === null)
      break;
    obj = v;
  }
  // 如果是空，返回[]；否则为 [一级header]/[二级header]/[三级header]...
  return path ? [path[0], path.join("/")] : [];
};

export const HeadersMenu: FC<HeadersMenuProps> = ({ headers, onChange }) => {
  const [open,] = openKey(headers);

  useEffect(() => {
    onChange?.(open ?? "");
  }, [onChange, open]);

  return <Menu mode="inline" style={menuStyle}
    defaultOpenKeys={open ? [open] : undefined}
    onSelect={({ key }) => onChange?.(key.toString())}>
    {renderItems(headers)}
  </Menu>;
};
