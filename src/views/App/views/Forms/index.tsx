import React, { FC, FormEvent, useState, useEffect } from "react";

import { Form, Button } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { getSection } from "@/api/forms";
import { PageLoading } from "@/components";

import { FormContext, Question } from "./Question";

const FormsD: FC<FormComponentProps> = ({ form }) => {

  const [source, setSource] = useState<Section>();

  const submit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) return;
      console.log("success");
    });
  };

  // 内部不能出现async函数，否则编译报错，不知道为何
  const loadFormLayout = (name: string) => {
    getSection({ name }).then(setSource);
  };

  useEffect(() => loadFormLayout("既往病史"), []);

  if (!source)
    return <PageLoading />;

  return (
    <Form onSubmit={submit}>
      {
        source.title && <Form.Item label="标题">
          { source.title }
        </Form.Item>
      }

      <FormContext.Provider value={form}>
        {
          source.questions.map(q =>
            <Question schema={q} key={q.field} />
          )
        }
      </FormContext.Provider>

      <Form.Item>
        <Button type="primary" htmlType="submit">提交</Button>
      </Form.Item>
    </Form>
  );

};

export const Forms = Form.create()(FormsD);

export default Forms;
