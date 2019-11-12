import React, { FC } from "react";

import {Button, Form} from "antd";
import { FormComponentProps, FormItemProps } from "antd/lib/form";

import { FormContext, Question } from "./Question";
import { Section } from "@/types/task";

interface P extends FormComponentProps {
  layout: Section;
  answer?: any;
  onChange?: (changedValues: any, allValues: any) => void;
  onSubmit?: (values: any) => void;
}

const QFormD: FC<P> = ({ layout: { questions }, onSubmit, form }) => {
  const style: FormItemProps = { labelCol: { span: 4 }, wrapperCol: { span: 14 } };

  const validated = () => {
    form.validateFieldsAndScroll((errors, values) => {
      if(!errors && onSubmit) onSubmit(values);
    });
  };

  return <Form layout="horizontal">
    <FormContext.Provider value={form}>
      {questions?.map(q => <Question schema={q} key={q.id} formItemProps={style} />)}
    </FormContext.Provider>
    <Form.Item wrapperCol={{ span: 14, offset: 4 }}>
      <Button onClick={validated} style={{marginLeft: 5}}>提交</Button>
    </Form.Item>
  </Form>;
};

export const QForm = Form.create<P>({
  mapPropsToFields({ answer }) {
    if(!answer) return {};
    return Object.assign({}, ...(Object.entries(answer).map(([k, value]) =>
      ({ [k]: Form.createFormField({ value }) })
    )));
  },

  onValuesChange({ onChange }, changed, all) {
    onChange?.(changed, all);
  }
})(QFormD);
