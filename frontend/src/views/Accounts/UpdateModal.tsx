import React, { FC } from "react";

import { Form, Input, Checkbox, Modal } from "antd";
import type { FormInstance } from "antd/es/form";
import {
  UserOutlined, LockOutlined, MailOutlined,
  UsergroupAddOutlined
} from "@ant-design/icons";

import { updateUser, UpdateUserRequest, CreateUserRequest, IdType } from "@/api";

interface UserFormProps {
  initial?: UpdateUserRequest & CreateUserRequest & { id?: IdType };
  isUpdate?: boolean;
  form: FormInstance;
}

const UserForm: FC<UserFormProps> = ({ initial, form }) => {
  return <Form initialValues={initial} form={form}>
    <Form.Item label="UID" name="id">
      <Input disabled/>
    </Form.Item>
    <Form.Item label="用户名" name="username">
      <Input prefix={<UserOutlined />} />
    </Form.Item>
    <Form.Item label="密码" name="password">
      <Input.Password prefix={<LockOutlined />} />
    </Form.Item>
    <Form.Item label="确认密码" name="confirm" dependencies={["password"]} hasFeedback
      rules={[
        ({ getFieldValue }) => ({
          required: !!getFieldValue("password"),
          validator(_, value) {
            if (!value || getFieldValue("password") === value)
              return Promise.resolve();
            return Promise.reject("两次输入的密码不一致！");

          }
        })
      ]}>
      <Input.Password prefix={<LockOutlined />} />
    </Form.Item>
    <Form.Item label="电子邮箱" name="email" rules={[
      {
        type: "email",
        message: "输入的电子邮箱地址不合法"
      }
    ]}>
      <Input prefix={<MailOutlined/>}/>
    </Form.Item>
    <Form.Item label="用户组名" name="group">
      <Input prefix={<UsergroupAddOutlined/>}/>
    </Form.Item>
    <Form.Item name="isAdmin">
      <Checkbox>是管理员</Checkbox>
    </Form.Item>
  </Form>;
}

interface UpdateModalProps {
  target: IdType;
}

export const UpdateModal: FC<UpdateModalProps> = ({ target }) => {
  const [form] = Form.useForm();

  const submit = async () => {
    let fields;
    try {
      fields = await form.validateFields();
    } catch (err) {
      console.log("errors");
      return;
    }
    await updateUser(target, fields);
  }

  return <Modal onOk={submit}>
    <UserForm form={form} />
  </Modal>;
};
