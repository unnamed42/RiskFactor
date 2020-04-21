import React, { FC, useEffect, createContext, useContext, useMemo } from "react";

import { Form, Button } from "antd";
import type { FormInstance } from "antd/es/form";
import type { Store } from "antd/es/form/interface";

import { RuleInfo, IdType, getAnswer } from "@/api";
import { Renderer } from "./components";
import { useApi } from "@/utils";

interface P {
  schema: Record<string, RuleInfo[]>;
  header: string;
  answers?: Store;
  answerId?: IdType;

  onFinish?: () => void;
  onValuesChange?: (changedValues: Store, values: Store) => void;
}

const FormContext = createContext({} as FormInstance);

export const useFormInstance = () => useContext(FormContext);

export const InternalForm: FC<P> = ({ schema, header, answers, answerId, onValuesChange, onFinish }) => {
  const [form] = Form.useForm();

  const [, fetchValues] = useApi(getAnswer, [], { immediate: false });

  // const [resp] = useApiCached(async () => {
  //   if (answerId === undefined)
  //     return {};
  //   return await getAnswer(answerId);
  // }, [answerId], {
  //   cacheKey: `answer-${answerId}`, mtimeGetter: answerModifiedAt
  // });

  const dom = useMemo(() => {
    const entries = Object.entries(schema).map(([headerKey, rules]) => {
      return [headerKey, <>
        {
          // TODO: namePath 设置为 header，以与问卷结构同步
          rules.map(rule => <Renderer rule={rule} key={rule.id} />)
        }
      </>] as [string, JSX.Element];
    });
    return new Map(entries);
  }, [schema]);

  useEffect(() => {
    if (answerId !== undefined)
      fetchValues(answerId).then(values => form.setFieldsValue(values));
  }, [answerId]);

  useEffect(() => {
    if(answerId !== undefined)
      form.setFields([{ name: ["#vars", "answerId"], value: answerId }]);
  }, [form, answerId]);

  return <Form form={form} layout="horizontal" labelCol={{ span: 4 }} wrapperCol={{ span: 14 }}
    onValuesChange={onValuesChange} validateMessages={{ required: "'${name}' 是必选字段" }}>
    {/* 插入 answerId 项 */}
    <Form.Item name={["#vars", "answerId"]} noStyle style={{ display: "none" }}>
      <></>
    </Form.Item>
    <FormContext.Provider value={form}>
      {dom.get(header) ?? null}
    </FormContext.Provider>
    <Form.Item>
      <Button type="primary" htmlType="submit" style={{ marginLeft: 5 }} onClick={onFinish}>提交</Button>
    </Form.Item>
  </Form>;
};
