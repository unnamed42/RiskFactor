import React, { FC, useState, useEffect } from "react";

import { Form, Radio, Input, Button } from "antd";

import { fieldUpdated } from "@/hooks";

export const Demo: FC = () => {

  const [value, _] = useState<string>();
  const [form] = Form.useForm();

  const namePath = ["$1"];
  const l = [...namePath, "label"];

  useEffect(() => {
    form.setFields([{ name: "fixed", value }])
  }, [value, form]);

  return <Form onFinish={values => console.log(values)} form={form}>
    <Form.Item label="item">
      <Form.Item noStyle name={[...namePath, "label"]}>
        <Radio.Group>
          <Radio value={1}>1</Radio>
          <Radio value={2}>2</Radio>
          <Radio value={3}>3</Radio>
          <Radio value={4}>
            4
            <Form.Item noStyle name={[...namePath, "other"]}>
              <Input />
            </Form.Item>
          </Radio>
        </Radio.Group>
      </Form.Item>
    </Form.Item>
    <Form.Item label="testa">
      <Form.Item noStyle name="fuck">
        <Input />
      </Form.Item>
    </Form.Item>
    <Form.Item noStyle shouldUpdate={fieldUpdated(l)}>
      {({ getFieldValue }) => {
        return getFieldValue(l) === 3 ?
          <Form.Item name={[...namePath, "input"]} label="input"><Input /></Form.Item> :
          null;
      }
      }
    </Form.Item>
    <Form.Item label="fixed" shouldUpdate={fieldUpdated("fuck")}>
      {/* eslint-disable-next-line @typescript-eslint/restrict-template-expressions */}
      {form => <Input value={`${form.getFieldValue("fuck")}-1`} disabled/> }
    </Form.Item>
    <Form.List name="list">
      {
        (fields, { add, remove }) => <>
          {
            fields.map((field, idx) => <React.Fragment key={field.key}>
              <Form.Item name={[idx, "item"]} label="item" >
                <Input />
              </Form.Item>
              <Form.Item name={[idx, "item2"]} label="item2">
                <Input />
              </Form.Item>
            </React.Fragment>)
          }
          <Button type="dashed" onClick={() => add()}>add</Button>
        </>
      }
    </Form.List>
    <Button htmlType="submit">submit</Button>
  </Form>;
};
