import React, { FC, useState } from "react";

import { assign } from "lodash";

import {Button, Form} from "antd";
import { FormComponentProps, FormItemProps } from "antd/lib/form";

import { FormContext, QSchema, Question } from "./Question";

interface P extends FormComponentProps {
  layout?: QSchema[];
  answer?: any;
  onChange?: (changedValues: any) => void;
  onSubmit?: (values: any) => Promise<void>;
}

const filteredEntries = (obj: any) =>
  Object.entries(obj).filter(([k]) => !k.startsWith("$"))
    .reduce((obj: any, [k, v]) => assign(obj, { [k]: v }), {});

const QFormD: FC<P> = ({ layout, onSubmit, form }) => {

  const style: FormItemProps = { labelCol: { span: 4 }, wrapperCol: { span: 14 } };

  const [posting, setPosting] = useState(false);

  const validated = () => {
    form.validateFieldsAndScroll((errors, values) => {
      if(errors || !onSubmit) return;
      setPosting(true);
      onSubmit(filteredEntries(values)).then(() => setPosting(false));
    });
  };

  return <Form layout="horizontal">
    <FormContext.Provider value={form}>
      {layout?.map(q => <Question schema={q} key={q.id} formItemProps={style} />)}
    </FormContext.Provider>
    <Form.Item wrapperCol={{ span: 14, offset: 4 }}>
      <Button onClick={validated} style={{marginLeft: 5}} disabled={posting} loading={posting}>提交</Button>
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
    onChange?.(filteredEntries(changed));
  }
})(QFormD);
