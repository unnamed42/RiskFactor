import React, { FC, useState, useEffect } from "react";

import { Form } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { Question, PageLoading } from "@/components";
import { fetch } from "@/api/forms";

const Forms: FC<FormComponentProps> = ({ form, ...props }) => {

  const [source, setSource] = useState<Section>();

  const submit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) return;
      console.log(values);
    });
  };

  useEffect(() => {
    (async () => {
      const response = await fetch({ name: "一般资料" });
      setSource(response);
      console.log(response);
    })();
  }, []);

  if (!source)
    return <PageLoading />;

  return (
    <Form onSubmit={submit}>
      <Form.Item label="标题">
        {source.title}
      </Form.Item>
      {source.questions.map((q, idx) =>
        (<Question key={q.field} schema={q} form={form} />))}
    </Form>
  );

};

const rendered = Form.create()(Forms);

export default rendered;
