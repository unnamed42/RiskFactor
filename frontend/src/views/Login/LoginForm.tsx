import React, { FC, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

import { Form, Input, Button, Checkbox, Tabs, message } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { Store } from "rc-field-form/es/interface";

import { StoreType } from "@/redux";
import * as authStore from "@/redux/auth";
import { login } from "@/api";
import { useAsync } from "@/utils";

import "./index.less";

// import { MobileFilled, MailOutlined } from "@ant-design/icons";
// import { phoneRegex } from "@/config";
// import { TimedButton } from "@/components";
// const PhoneValidatorPane: FC = () => <>
//   <Form.Item name="phone" rules={[{ required: true, message: "请输入手机号码！", pattern: phoneRegex }]}>
//     <Input prefix={<MobileFilled className="login-form-icon" />} placeholder="手机号码" />
//   </Form.Item>
//   <Form.Item>
//     <span>
//       <Form.Item noStyle name="captcha" rules={[{ required: true, message: "请输入验证码！" }]}>
//         <Input prefix={<MailOutlined className="login-form-icon" />} placeholder="验证码"
//           style={{ width: "65%", marginRight: "3%" }} />
//       </Form.Item>
//       <TimedButton interval={1} text="获取验证码" style={{ width: "32%", textAlign: "center" }} />
//     </span>
//   </Form.Item>
// </>;

export const LoginForm: FC = () => {
  const auth = useSelector((store: StoreType) => store.auth);
  const dispatch = useDispatch();

  const [state, requestLogin] = useAsync(login, []);

  const doLogin = (values: Store) => {
    const { username, password } = values;
    requestLogin({ username, password });
  };

  useEffect(() => {
    if (state && !state.loading) {
      if (state.error !== undefined)
        message.error(state.error.message);
      else
        // 外层根据token的更新会重定向至Referer页面
        dispatch(authStore.login(state.data));
    }
  }, [state, dispatch]);

  const initialValues = {
    username: auth.token !== null ? auth.username : undefined,
    remember: false
  };

  return <Form onFinish={doLogin} className="login-form" initialValues={initialValues}>
    <Tabs defaultActiveKey="1" animated={{ inkBar: true, tabPane: false }} tabBarStyle={{ textAlign: "center" }}>
      <Tabs.TabPane tab="用户名密码登录" key="1">
        <Form.Item name="username" rules={[{
          required: true, message: "请输入用户名！",
          validateTrigger: ["onChange", "onBlur"]
        }]}>
          <Input placeholder="用户名" prefix={<UserOutlined className="login-form-icon" />} />
        </Form.Item>
        <Form.Item name="password" rules={[{
          required: true, message: "请输入密码！",
          validateTrigger: ["onChange", "onBlur"]
        }]}>
          <Input.Password prefix={<LockOutlined className="login-form-icon" />} type="password" placeholder="密码" />
        </Form.Item>
      </Tabs.TabPane>
    </Tabs>
    <Form.Item noStyle>
      <Form.Item noStyle valuePropName="checked" name="remember">
        <Checkbox className="login-form-remember">保持登录</Checkbox>
      </Form.Item>
      <a className="login-form-forget" href="">忘记密码？</a>
      <Button type="primary" htmlType="submit" className="login-form-submit"
        disabled={state?.loading} loading={state?.loading}>登录</Button>
    </Form.Item>
  </Form>;
};
