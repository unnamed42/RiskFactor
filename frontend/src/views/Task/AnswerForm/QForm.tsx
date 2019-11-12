import React, { Component } from "react";

import {Button, Form} from "antd";
import { FormComponentProps } from "antd/lib/form";

import { FormContext, Question } from "./Question";
import { Section } from "@/types/task";

interface P extends FormComponentProps {
  layout: Section;
  answer?: any;
  onSave?: () => void;
  onSubmit?: () => void;
}

// 为了提供对外的方法，设置成class component
export class QFormD extends Component<P> {

  validatedValues(): Promise<any> {
    return new Promise((resolve, reject) => {
      const { validateFieldsAndScroll, getFieldsValue } = this.props.form;
      validateFieldsAndScroll((errors, _) => {
        if (errors) reject(errors);
        else resolve(getFieldsValue());
      });
    });
  }

  render() {
    const { layout: { questions }, form, onSubmit, onSave } = this.props;
    const style = { labelCol: { span: 4 }, wrapperCol: { span: 14 } };
    return <Form layout="horizontal">
      <FormContext.Provider value={form}>
        {
          questions?.map(q =>
            <Question schema={q} key={q.id}
              formItemProps={style}
            />
          )
        }
        <Form.Item wrapperCol={{ span: 14, offset: 4 }}>
          <Button onClick={() => onSave?.()} style={{marginLeft: 5}}>提交</Button>
        </Form.Item>
      </FormContext.Provider>
    </Form>;
  }

}

export const QForm = Form.create<P>({
  mapPropsToFields({ answer }) {
    return answer ? Object.assign({}, Object.keys(answer).map(k => ({
      [k]: Form.createFormField({ value: answer[k] })
    }))) : {};
  }
})(QFormD);
