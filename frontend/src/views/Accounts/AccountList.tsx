import React, { FC, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useHistory } from "react-router";

import { Table, Button, Popconfirm } from "antd";
import { CheckOutlined } from "@ant-design/icons";
// import type { TableRowSelection } from "antd/es/table/interface";

import { userInfoList, deleteUser as du, UserInfo } from "@/api";
import type { StoreType } from "@/redux";
import { logout } from "@/redux/auth";
import { useApi, useData } from "@/hooks";

const renderIsAdmin = (isAdmin: boolean | undefined) =>
  isAdmin ? <CheckOutlined /> : null;

export const AccountList: FC = () => {
  const [resp] = useApi(userInfoList, []);
  const [, deleteUser] = useApi(du, [], { success: "删除成功", immediate: false });

  const auth = useSelector((state: StoreType) => state.auth);
  const dispatch = useDispatch();
  const history = useHistory();
  const [_, setSelected] = useState<Array<number | string>>([]);
  const [source, setSource] = useData(resp);

  if (resp.alt !== undefined)
    return resp.alt;

  // const rowSelection: TableRowSelection<UserInfo> = {
  //   type: "checkbox",
  //   selectedRowKeys: selected,
  //   onChange: setSelected
  // };

  const renderAction = (_: any, item: UserInfo) => {
    const deleteThis = async () => {
      await deleteUser(item.id);
      setSource(prevSource => prevSource?.filter(elem => elem.id != item.id));
      setSelected(prevSelected => prevSelected.filter(elem => elem != item.id));
      if (auth.token !== null && item.id == auth.userId) {
        dispatch(logout());
        history.replace("/login");
      }
    };
    return <span>
      <Popconfirm title="确认删除？" onConfirm={deleteThis} okText="是" cancelText="否">
        <Button type="link" danger>删除</Button>
      </Popconfirm>
    </span>;
  };

  return <Table dataSource={source} loading={source === undefined}
                // rowSelection={rowSelection}
                tableLayout="fixed" rowKey="id">
    <Table.Column align="center" title="UID" dataIndex="id" />
    <Table.Column align="center" title="用户名" dataIndex="username" />
    <Table.Column align="center" title="电子邮箱" dataIndex="email" />
    <Table.Column align="center" title="用户组" dataIndex="group" />
    <Table.Column align="center" title="是管理员" dataIndex="isAdmin" render={renderIsAdmin} />
    <Table.Column align="center" title="操作" key="action" render={renderAction}/>
  </Table>;
};
