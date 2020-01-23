import React, { FC, useState } from "react";

import { Button, Form } from "antd";
import { ValidateErrorEntity } from "rc-field-form/es/interface";

import { Question } from "@/types";
import { Renderer } from "./components";

interface P {
  rules: Question[];
  answer?: any;
  onChange?: (changedValues: any, allValues?: any) => void;
  onSubmit?: () => Promise<void>;
}

export const QForm: FC<P> = ({ rules, onChange, onSubmit }) => {
  const [posting, setPosting] = useState(false);
  const [form] = Form.useForm();

  const validated = () => form.validateFields()
    .then(values => {
      setPosting(true);
      return onSubmit?.().then(() => setPosting(false));
    }, (error: ValidateErrorEntity) => {
      const field = error.errorFields[0].name;
      form.scrollToField(field);
    });

  return <Form layout="horizontal" labelCol={{ span: 4 }} wrapperCol={{ span: 14 }} form={form}
               onValuesChange={onChange} validateMessages={{ required: "'${name}' 是必选字段" }}>
    <Form.Item noStyle style={{ display: "none" }} name="#vars.answerId" children={<></>}/>
    {rules.map(rule => <Renderer rule={rule} key={rule.id} />)}
    <Form.Item>
      <Button onClick={validated} style={{ marginLeft: 5 }} disabled={posting} loading={posting}>提交</Button>
    </Form.Item>
  </Form>;
};
