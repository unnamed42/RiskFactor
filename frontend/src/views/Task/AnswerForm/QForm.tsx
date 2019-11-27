import React, { FC, useState } from "react";

import { assign, isPlainObject } from "lodash";

import { Button, Form } from "antd";
import { FormComponentProps, FormItemProps } from "antd/lib/form";

import { FormContext, QSchema, Question } from "./Question";

interface P extends FormComponentProps {
  layout?: QSchema[];
  answer?: any;
  onChange?: (changedValues: any, allValues?: any) => void;
  onSubmit?: () => Promise<void>;
}

const QFormD: FC<P> = ({ layout, onSubmit, form }) => {

  const style: FormItemProps = { labelCol: { span: 4 }, wrapperCol: { span: 14 } };

  const [posting, setPosting] = useState(false);

  const validated = () => {
    form.validateFieldsAndScroll((errors, _) => {
      if (errors || !onSubmit) return;
      setPosting(true);
      onSubmit().then(() => setPosting(false));
    });
  };

  // 预设变量：当前问题id
  form.getFieldDecorator("#vars.answerId");

  return <Form layout="horizontal">
    <FormContext.Provider value={form}>
      {layout?.map(q => <Question schema={q} key={q.id} formItemProps={style}/>)}
    </FormContext.Provider>
    <Form.Item wrapperCol={{ span: 14, offset: 4 }}>
      <Button onClick={validated} style={{ marginLeft: 5 }} disabled={posting} loading={posting}>提交</Button>
    </Form.Item>
  </Form>;
};

const transform = <T extends any = any>(obj: T): any =>
  Object.entries(obj).reduce((acc, [k, v]) => {
    if(v === undefined) return acc;
    const asignee = (() => {
      if(isPlainObject(v))
        return transform(v);
      return Form.createFormField({ value: v });
    })();
    return assign(acc, { [k]: asignee });
  }, {});

export const QForm = Form.create<P>({
  mapPropsToFields: ({ answer }) => answer ? transform(answer) : {},
  onValuesChange: ({ onChange }, changed, all) => onChange?.(changed, all)
})(QFormD);
