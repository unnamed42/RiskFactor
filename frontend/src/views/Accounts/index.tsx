import React, { FC, CSSProperties, useState } from "react";
import { useSelector } from "react-redux";

import { Form, Button, Modal } from "antd";
import { PlusOutlined, UserSwitchOutlined } from "@ant-design/icons";

import { AccountList } from "./AccountList";
import { AccountForm } from "./AccountForm";
import { useAsync } from "@/hooks";
import { updateUser, createUser, IdType } from "@/api";
import type { StoreType } from "@/redux";

const buttonLine: CSSProperties = {
  marginBottom: 16
};

const button: CSSProperties = {
  marginRight: 5
};

export const Accounts: FC = () => {
  const [visible, setVisible] = useState(false);
  const [target, setTarget] = useState<IdType>();

  const auth = useSelector((store: StoreType) => store.auth);
  const [form] = Form.useForm();

  const [state, submit] = useAsync(async () => {
    let fields;
    try {
      fields = await form.validateFields();
    } catch (err) {
      console.log("errors");
      return;
    }
    const request = target === undefined ?
      createUser(fields as any) :
      updateUser(target, fields);
    await request;
    setVisible(false);
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
