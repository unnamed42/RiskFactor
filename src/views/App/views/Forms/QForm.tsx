import React, { Component } from "react";

import { Form } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { PageLoading } from "@/components";

import { FormContext, Question } from "./Question";

interface P extends FormComponentProps {
  source?: Section;
  answer?: any;
}

export const repackAnswer = (values: any) => {
  const ret: any = {};
  Object.keys(values).forEach(key => {
    const prop = values[key];
    if ("field" in prop) {
      const value = prop.value;
      ret[key] = Array.isArray(value) ? value.map(repackAnswer) : value;
    } else
      ret[key] = repackAnswer(prop);
  });
  return ret;
};

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
    const { source, form } = this.props;
    if (!source)
      return <PageLoading />;
    return <Form layout="horizontal">
      <FormContext.Provider value={form}>
        {
          source.questions && source.questions.map(q =>
            <Question schema={q} key={q.field} />
          )
        }
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
