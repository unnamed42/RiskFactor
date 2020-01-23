import React, { FormEvent, FC, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import { Form, Input, Button, Checkbox, message } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";

import { StoreType } from "@/redux";
import * as authStore from "@/redux/auth";
import { login } from "@/api/auth";

import "./index.less";

interface Fields {
  username: string;
  password: string;
  remember: boolean;
}

interface P {
  onLoginSuccess?: () => void;
}

export const LoginForm: FC<P> = ({ onLoginSuccess }) => {
  const auth = useSelector((store: StoreType) => store.auth);
  const [logging, setLogging] = useState(false);
  const dispatch = useDispatch();
  const [form] = Form.useForm();

  // 复杂逻辑用async-await转译的代码有问题
  const doLogin = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    form.validateFields(["username", "password", "remember"]).then(values => {
      const { username, password } = values as Fields;
      setLogging(true);
      return login(username, password)
        .then(
          token => { dispatch(authStore.login(token)); onLoginSuccess?.(); },
          err => { message.error(err.message); })
        .finally(() => setLogging(false));
    });
  };

  const init: Partial<Fields> = {
    username: auth.token !== null ? auth.username : undefined
  };

  return <Form onSubmitCapture={doLogin} className="login-form" initialValues={init}>
    <Form.Item name="username" rules={[{
      required: true, message: "请输入用户名！",
      validateTrigger: ["onChange", "onBlur"] }]}>
      <Input placeholder="用户名" prefix={<UserOutlined className="login-form-icon" />} />
    </Form.Item>
    <Form.Item name="password" rules={[{
      required: true, message: "请输入密码！",
      validateTrigger: ["onChange", "onBlur"] }]}>
      <Input.Password prefix={<LockOutlined className="login-form-icon"/>} type="password" placeholder="密码"/>
    </Form.Item>
    <Form.Item>
      <Form.Item noStyle valuePropName="checked" name="remember">
        <Checkbox className="login-form-remember">保持登录</Checkbox>
      </Form.Item>
      <a className="login-form-forget" href="">忘记密码？</a>
      <Button type="primary" htmlType="submit" className="login-form-submit"
              disabled={logging} loading={logging}>登录</Button>
    </Form.Item>
  </Form>;
};

// import { Tabs } from "antd";
// import { MobileFilled, MailOutlined } from "@ant-design/icons";
// import { phoneRegex } from "@/config";
// import { TimedButton } from "@/components";
//
// const FormLegacy: FC = () => {
//   const [tab, setTab] = useState("1");
//   return <Tabs defaultActiveKey={tab} onChange={setTab} animated={{ inkBar: true, tabPane: false }}
//                tabBarStyle={{ textAlign: "center" }}>
//     <Tabs.TabPane tab="短信验证码登录" key="2">
//       <Form.Item name="phone" rules={[{ required: true, message: "请输入手机号码！", pattern: phoneRegex }]}>
//         <Input prefix={<MobileFilled className="login-form-icon"/>} placeholder="手机号码"/>
//       </Form.Item>
//       <Form.Item>
//         <span>
//           <Form.Item noStyle name="captcha" rules={[{ required: true, message: "请输入验证码！" }]}>
//             <Input prefix={<MailOutlined className="login-form-icon"/>} placeholder="验证码"
//                    style={{ width: "65%", marginRight: "3%" }}/>
//           </Form.Item>
//           <TimedButton interval={1} text="获取验证码" style={{ width: "32%", textAlign: "center" }}/>
//         </span>
//       </Form.Item>
//     </Tabs.TabPane>
//   </Tabs>;
// };
