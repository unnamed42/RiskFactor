import React, { Component } from "react";

import { Form } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { PageLoading } from "@/components";

import { FormContext, Question } from "./Question";

interface P extends FormComponentProps {
  source?: Section;
}

const repackAnswer = (values: any) => {
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

interface FormFields {
  errors: any;
  values: any;
}

// to use refs elegantly, has to be class component
export class QFormD extends Component<P> {

  submit(): Promise<FormFields> {
    return new Promise((resolve, _) => {
      this.props.form.validateFieldsAndScroll((errors, values) =>
        resolve({ errors, values })
      );
    });
  }

  render() {
    const { source, form } = this.props;
    if (!source)
      return <PageLoading />;
    return <Form layout="horizontal">
      <FormContext.Provider value={form}>
        {
          source.questions.map(q =>
            <Question schema={q} key={q.field} />
          )
        }
      </FormContext.Provider>
    </Form>;
  }
}

export const QForm = Form.create<P>()(QFormD);
