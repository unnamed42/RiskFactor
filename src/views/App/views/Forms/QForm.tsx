import React, { FC, FormEvent } from "react";

import { Form, Button } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { PageLoading } from "@/components";

import { FormContext, Question } from "./Question";

type P = FormComponentProps & {
  source?: Section;
};

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

const QFormD: FC<P> = ({ source, form }) => {

  const submit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) return;
      console.log(repackAnswer(values));
    });
  };

  if (!source)
    return <PageLoading />;

  return (
    <Form onSubmit={submit} layout="horizontal">
     <FormContext.Provider value={form}>
        {
          source.questions.map(q =>
            <Question schema={q} key={q.field} />
          )
        }
      </FormContext.Provider>

      <Form.Item wrapperCol={{span: 14, offset: 4}}>
        <Button type="primary" htmlType="submit">提交</Button>
      </Form.Item>
    </Form>
  );

};

export const QForm = Form.create<P>()(QFormD);
