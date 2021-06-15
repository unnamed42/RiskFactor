import React, { FC, CSSProperties, useState } from "react";
import { useSelector } from "react-redux";

import { Form, Button, Modal, message } from "antd";
import { PlusOutlined, UserSwitchOutlined } from "@ant-design/icons";

import { AccountList } from "./AccountList";
import { AccountForm } from "./AccountForm";
import { useAsync } from "@/hooks";
import { updateUser, createUser, IdType } from "@/api";
import type { CreateUserRequest, UpdateUserRequest } from "@/api";
import type { StoreType } from "@/redux";

const buttonLine: CSSProperties = {
  marginBottom: 16
};

const button: CSSProperties = {
  marginRight: 5
};

/**
 * 用户管理界面：添加/删除按钮、用户列表、信息修改弹窗
 */
export const Accounts: FC = () => {
  const [visible, setVisible] = useState(false);
  const [target, setTarget] = useState<IdType>();

  const auth = useSelector((store: StoreType) => store.auth);
  const [form] = Form.useForm<UpdateUserRequest | CreateUserRequest>();

  const [state, submit] = useAsync(async () => {
    let fields;
    try {
      fields = await form.validateFields();
    } catch (err) {
      void message.error((err as Error).message);
      return;
    }
    await (target === undefined ?
      createUser(fields as CreateUserRequest) : updateUser(target, fields)
    );
    setVisible(false);
    // TODO: 使用一种更加合适的刷新当前组件的方法
    window.location.reload();
  }, [target]);

  const triggerModal = () => {
    setVisible(true);
    if (auth.token !== null)
      setTarget(auth.userId);
  };

  return <div>
    <div style={buttonLine}>
      <Button type="primary" style={button} onClick={() => setVisible(true)} icon={<PlusOutlined />}>
        添加用户
      </Button>
      <Button type="primary" onClick={triggerModal} icon={<UserSwitchOutlined/>}>
        修改信息
      </Button>
    </div>
    <Modal visible={visible} centered closable={false} confirmLoading={state?.loading}
      onOk={submit} onCancel={() => setVisible(false)} destroyOnClose={true}
      okText="提交" cancelText="取消" title={target == undefined ? "创建用户" : "更新用户"}>
      <AccountForm form={form} target={target} />
    </Modal>
    <AccountList/>
  </div>;
};

export default Accounts;
