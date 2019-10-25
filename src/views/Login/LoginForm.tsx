import React, { FormEvent, FC, useState } from "react";
import { useDispatch } from "react-redux";

import { Form, Icon, Input, Button, Checkbox, Tabs } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { TimedButton } from "@/components";
import { login as auth_login } from "@/redux/auth";
import { LoginPayload } from "@/api/login";

import "./index.less";

// 匹配所有号码（手机卡 + 数据卡 + 上网卡） https://github.com/VincentSit/ChinaMobilePhoneNumberRegex
const phoneRegex = /^(?:\+?86)?1(?:3\d{3}|5[^4\D]\d{2}|8\d{3}|7(?:[01356789]\d{2}|4(?:0\d|1[0-2]|9\d))|9[189]\d{2}|6[567]\d{2}|4(?:[14]0\d{3}|[68]\d{4}|[579]\d{2}))\d{6}$/;

type TabFields<T> = T & { remember: boolean; };
type UsernameTabFields = LoginPayload;
type PhoneTabFields = { phone: string; captcha: string };

type P = FormComponentProps<TabFields<UsernameTabFields> | TabFields<PhoneTabFields>> & {
  onLoginSuccess?: () => void;
};

const LoginFormD: FC<P> = props => {

  const [tab, setTab] = useState("1");
  const dispatch = useDispatch();

  const login = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (tab === "1") {
      // username login
      props.form.validateFields(["username", "password", "remember"], (err, values) => {
        if (err) return;
        dispatch(auth_login(values as TabFields<UsernameTabFields>, props.onLoginSuccess));
      });
    } else {
      props.form.validateFields(["phone", "captcha", "remember"], (err, values) => {
        if (err) return;
        // dispatch({ type: "auth/login-phone", payload: values as FormFields<PhoneFields> });
      });
    }
  };

  const { getFieldDecorator } = props.form;

  return (
    <Form onSubmit={login} className="login-form">
      <Tabs defaultActiveKey={tab} onChange={setTab} animated={{ inkBar: true, tabPane: false }}
            tabBarStyle={{ textAlign: "center" }}>
        <Tabs.TabPane tab="用户名登录" key="1">
          <Form.Item>
            {
              getFieldDecorator("username", {
                rules: [{ required: true, message: "请输入用户名！" }],
                validateTrigger: ["onChange", "onBlur"]
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
              <Input prefix={<Icon type="mobile" theme="filled" className="login-form-icon" />}
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
                <Input prefix={<Icon type="mail" className="login-form-icon" />}
                    placeholder="验证码" style={{ width: "65%", marginRight: "3%" }}/>
                )
              }
              <TimedButton interval={1} text="获取验证码" style={{ width: "32%", textAlign: "center" }}/>
            </span>
          </Form.Item>
        </Tabs.TabPane>
      </Tabs>

      <Form.Item>
        { getFieldDecorator("remember", {
          valuePropName: "checked", initialValue: false
        })(<Checkbox className="login-form-remember">保持登录</Checkbox>) }
        <a className="login-form-forget" href="">忘记密码？</a>
        <Button type="primary" htmlType="submit" className="login-form-submit">登录</Button>
      </Form.Item>
    </Form>
  );
};

export const LoginForm = Form.create<P>()(LoginFormD);
