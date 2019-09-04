import React, { Component, FormEvent } from "react";

import { Form, Icon, Input, Button, Checkbox } from "antd";
import { FormComponentProps } from "antd/lib/form";

import "./index.less";

class LoginForm extends Component<FormComponentProps> {
  handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) return;
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSubmit} className="login-form">
        <Form.Item>
          { getFieldDecorator("username", {
            rules: [{ required: true, message: "请输入用户名！" }],
            validateTrigger: ["onChange", "onBlur"]
          })(
            <Input prefix={<Icon type="user" className="login-form-icon"/>}
                   placeholder="用户名"/>
          ) }
        </Form.Item>
        <Form.Item>
          { getFieldDecorator("password", {
            rules: [{ required: true, message: "请输入密码！" }],
            validateTrigger: ["onChange", "onBlur"]
          })(
            <Input prefix={<Icon type="lock" className="login-form-icon"/>}
                   type="password" placeholder="密码"/>
          ) }
        </Form.Item>
        <Form.Item>
          { getFieldDecorator("remember", {
            valuePropName: "checked", initialValue: true
          })(<Checkbox>保持登录</Checkbox>) }
          <a className="login-form-forget" href="">忘记密码？</a>
          <Button type="primary" htmlType="submit" className="login-form-submit">登录</Button>
        </Form.Item>
      </Form>
    );
  }
}

export default Form.create()(LoginForm);
