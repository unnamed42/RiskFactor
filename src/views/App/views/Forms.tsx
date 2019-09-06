import React, { FC } from "react";

import { Form, DatePicker, Button, Checkbox, Radio } from "antd";
import { FormComponentProps } from "antd/lib/form";

import { Moment } from "moment";

import { CheckedInput, EthnicSelect, CheckedContent } from "@/components";

interface FormProps {
  birth: Moment;
  ethnic: string;
  disease: string[];
  severity?: number;
  height: string;
}

const Forms: FC<FormComponentProps<FormProps>> = props => {

  const submit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    props.form.validateFields((err, values) => {
      if (err) return;
      const birth = values.birth.format("YYYY-MM-DD");
    });
  };

  const { getFieldDecorator } = props.form;

  return (
    <Form labelCol={{ span: 8 }} wrapperCol={{ span: 8 }}
          onSubmit={submit} hideRequiredMark={true}>

      <Form.Item label="中心编号">
        <span className="ant-form-text">0001</span>
      </Form.Item>

      <Form.Item label="出生日期">
        {
          getFieldDecorator("birth", {
            rules: [{ required: true, message: "请填写出生日期" }]
          })(<DatePicker style={{ width: "100%" }} placeholder="出生日期"/>)
        }
      </Form.Item>

      <Form.Item label="民族">
        {
          getFieldDecorator("ethnic", {
            rules: [{ required: true, message: "请选择民族" }]
          })(<EthnicSelect/>)
        }
      </Form.Item>

      <Form.Item label="病史">
        {
          getFieldDecorator("disease", {
            initialValue: []
          })(
            <Checkbox.Group style={{ width: "100%" }}>
              <Checkbox value="腹痛">腹痛</Checkbox>
              <Checkbox value="黄疸">黄疸</Checkbox>
              <Checkbox value="恶心呕吐">恶心呕吐</Checkbox>
            </Checkbox.Group>
          )
        }
      </Form.Item>

      <Form.Item label="其他">

        <CheckedContent text="胆囊炎">
          {
            getFieldDecorator("severity", {

            })(
            <Radio.Group>
                <Radio value={1}>急性</Radio>
                <Radio value={2}>慢性</Radio>
              </Radio.Group>
            )
          }

            </CheckedContent>

      </Form.Item>

      <Form.Item label="身高">
        {
          getFieldDecorator("height", {
            rules: [{ required: true, message: "请填写身高" }]
          })(
            <CheckedInput rule={/^(0|[1-9]\d*)(\.\d*)?$/}
                          placeholder="身高" addonAfter="m" />
          )
        }
      </Form.Item>

      <Form.Item wrapperCol={{ span: 14, offset: 8 }}>
        <Button type="primary" htmlType="submit">
          提交
        </Button>
      </Form.Item>

    </Form>
  );
};

export default Form.create()(Forms);
