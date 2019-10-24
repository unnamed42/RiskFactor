import React, { FC, FormEvent, useState, useEffect } from "react";

import { Form, Button } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { getSection } from "@/api/forms";
import { PageLoading, Question } from "@/components";
import { decorator } from "@/components/Question";

const FormsD: FC<FormComponentProps> = ({ form }) => {

  const [source, setSource] = useState(null as Section | null);

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

  useEffect(() => loadFormLayout("一般资料"), []);

  if (!source)
    return <PageLoading />;

  return (
    <Form onSubmit={submit}>
      <Form.Item label="标题">
        {source.title || "title"}
      </Form.Item>

      {
        source.questions.map((q, idx) =>
          <Form.Item label={q.label} key={idx}>
            {
              form.getFieldDecorator(q.field, decorator(q))(
                <Question schema={q}/>
              )
            }
          </Form.Item>
        )
      }

      <Form.Item>
        <Button type="primary" htmlType="submit">提交</Button>
      </Form.Item>
    </Form>
  );

};

export const Forms = Form.create()(FormsD);
export default Forms;
