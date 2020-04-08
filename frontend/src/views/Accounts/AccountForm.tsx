import React, { FC, useEffect } from "react";

import { Form, Input, Checkbox, Select } from "antd";
import type { FormInstance } from "antd/es/form";
import {
  UserOutlined, LockOutlined, MailOutlined
} from "@ant-design/icons";

import { IdType, groupNames, userInfo } from "@/api";
import { useApi } from "@/utils";

interface UserFormProps {
  target?: IdType;
  form: FormInstance;
}

const formStyle = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

export const AccountForm: FC<UserFormProps> = ({ target, form }) => {
  const [resp] = useApi(groupNames, []);
  const [info] = useApi(async () => {
    if (target !== undefined)
      return await userInfo(target);
    return {};
  }, [target]);

  useEffect(() => {
    if (info.alt === undefined)
      form.setFieldsValue(info.data);
  }, [form, info]);

  return <Form form={form} {...formStyle}>
    <Form.Item label="UID" name="id">
      <Input disabled />
    </Form.Item>
    <Form.Item label="用户名" name="username" required>
      <Input prefix={<UserOutlined />} />
    </Form.Item>
    <Form.Item label="密码" name="password" required>
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
      <Input prefix={<MailOutlined />} />
    </Form.Item>
    <Form.Item label="用户组名" name="group" required>
      <Select loading={resp.alt !== undefined} disabled={target !== undefined}>
        {
          resp.alt === undefined ?
            resp.data.map((name, idx) => <Select.Option key={idx} value={name}>{name}</Select.Option>) :
            null
        }
      </Select>
    </Form.Item>
    <Form.Item name="isAdmin" valuePropName="checked">
      <Checkbox disabled={target !== undefined}>是管理员</Checkbox>
    </Form.Item>
  </Form>;
};
