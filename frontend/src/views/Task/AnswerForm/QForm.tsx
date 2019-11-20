import React, { FC } from "react";

import {Button, Form} from "antd";
import { FormComponentProps, FormItemProps } from "antd/lib/form";

import { FormContext, QSchema, Question } from "./Question";

interface P extends FormComponentProps {
  layout?: QSchema[];
  answer?: any;
  onChange?: (changedValues: any, allValues: any) => void;
  onSubmit?: (values: any) => void;
}

const QFormD: FC<P> = ({ layout, onSubmit, form }) => {
  const style: FormItemProps = { labelCol: { span: 4 }, wrapperCol: { span: 14 } };

  const validated = () => {
    form.validateFieldsAndScroll((errors, values) => {
      if(!errors) onSubmit?.(values);
    });
  };

  return <Form layout="horizontal">
    <FormContext.Provider value={form}>
      {layout?.map(q => <Question schema={q} key={q.id} formItemProps={style} />)}
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
