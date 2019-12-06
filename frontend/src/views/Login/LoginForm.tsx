import React, { FormEvent, FC, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import { Form, Icon, Input, Button, Checkbox, Tabs, message } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { TimedButton } from "@/components";
import { StoreType } from "@/redux";
import * as authStore from "@/redux/auth";
import { login } from "@/api/auth";
import { phoneRegex } from "@/config";

import "./index.less";

type Fields = { remember: boolean; } & ({
  username: string; password: string;
} | {
  phone: string; captcha: string;
});

interface P extends FormComponentProps<Fields> {
  onLoginSuccess?: () => void;
}

const LoginFormD: FC<P> = ({ form: { validateFields, getFieldDecorator } , onLoginSuccess }) => {

  const auth = useSelector((store: StoreType) => store.auth);

  const [tab, setTab] = useState("1");
  const [logging, setLogging] = useState(false);
  const dispatch = useDispatch();

  // 内部用async-await有渲染问题
  const doLogin = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if(tab !== "1") return;
    validateFields(["username", "password", "remember"], (error, values) => {
      if (error || !("username" in values)) return;
      const { username, password } = values;
      setLogging(true);
      login(username, password)
        .then(token => { dispatch(authStore.login(token)); onLoginSuccess?.(); })
        .catch(ee => { setLogging(false); message.error(ee.message); });
    });

    //   form.validateFields(["phone", "captcha", "remember"], (err, values) => {
    //     if (err) return;
    //     // dispatch({ type: "auth/login-phone", payload: values as FormFields<PhoneFields> });
    //   });
    // }
  };

  return <Form onSubmit={doLogin} className="login-form">
    <Tabs defaultActiveKey={tab} onChange={setTab} animated={{ inkBar: true, tabPane: false }}
          tabBarStyle={{ textAlign: "center" }}>
      <Tabs.TabPane tab="用户名登录" key="1">
        <Form.Item>
          {
            getFieldDecorator("username", {
              rules: [{ required: true, message: "请输入用户名！" }],
              validateTrigger: ["onChange", "onBlur"],
              initialValue: auth.token !== null ? auth.username : undefined
            })(
              <Input prefix={<Icon type="user" className="login-form-icon"/>}
                     placeholder="用户名"/>
            )
          }
        </Form.Item>
        <Form.Item>
          {
            getFieldDecorator("password", {
              rules: [{ required: true, message: "请输入密码！" }],
              validateTrigger: ["onChange", "onBlur"]
            })(
              <Input.Password prefix={<Icon type="lock" className="login-form-icon"/>}
                              type="password" placeholder="密码"/>
            )
          }
        </Form.Item>
      </Tabs.TabPane>

      <Tabs.TabPane tab="短信验证码登录" key="2">
        <Form.Item>
          {
            getFieldDecorator("phone", {
              rules: [{ required: true, message: "请输入手机号码！", pattern: phoneRegex }],
              validateTrigger: ["onChange", "onBlur"]
            })(
              <Input prefix={<Icon type="mobile" theme="filled" className="login-form-icon"/>}
                     placeholder="手机号码"/>
            )
          }
        </Form.Item>
        <Form.Item>
            <span>
              {
                getFieldDecorator("captcha", {
                  rules: [{ required: true, message: "请输入验证码！" }]
                })(
                  <Input prefix={<Icon type="mail" className="login-form-icon"/>}
                         placeholder="验证码" style={{ width: "65%", marginRight: "3%" }}/>
                )
              }
              <TimedButton interval={1} text="获取验证码" style={{ width: "32%", textAlign: "center" }}/>
            </span>
        </Form.Item>
      </Tabs.TabPane>
    </Tabs>

    <Form.Item>
      {getFieldDecorator("remember", {
        valuePropName: "checked", initialValue: false
      })(<Checkbox className="login-form-remember">保持登录</Checkbox>)}
      <a className="login-form-forget" href="">忘记密码？</a>
      <Button type="primary" htmlType="submit" className="login-form-submit"
              disabled={logging} loading={logging}>登录</Button>
    </Form.Item>
  </Form>;
};

export const LoginForm = Form.create<P>()(LoginFormD);
