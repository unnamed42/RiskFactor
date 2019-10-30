import React, { Component } from "react";

import {Button, Form} from "antd";
import { FormComponentProps } from "antd/lib/form";

import { FormContext, Question } from "./Question";
import { Section } from "@/types/task";

interface P extends FormComponentProps {
  source: Section;
  answer?: any;
  onSubmit?: () => void;
}

// to use refs elegantly, has to be class component
export class QFormD extends Component<P> {

  submit(): Promise<any> {
    return new Promise((resolve, reject) => {
      const { validateFieldsAndScroll, getFieldsValue } = this.props.form;
      validateFieldsAndScroll((errors, _) => {
        if (errors) reject(errors);
        else resolve(getFieldsValue());
      });
    });
  }

  render() {
    const { source: { questions }, form, onSubmit } = this.props;
    const style = { labelCol: { span: 4 }, wrapperCol: { span: 14 } };
    return <Form layout="horizontal">
      <FormContext.Provider value={form}>
        {
          questions && questions.map(q =>
            <Question schema={q} key={q.id}
              formItemProps={style}
            />
          )
        }
        <Form.Item wrapperCol={{ span: 14, offset: 4 }}>
          <Button type="primary" onClick={() => onSubmit && onSubmit()}>提交</Button>
        </Form.Item>
      </FormContext.Provider>
    </Form>;
  }

}

export const QForm = Form.create<P>({
  mapPropsToFields({ answer }) {
    console.log(answer);
    return Object.keys(answer || {}).reduce((prev, k) =>
      ({ ...prev, [k]: Form.createFormField({ value: answer[k] }) })
    , {});
  }
})(QFormD);
