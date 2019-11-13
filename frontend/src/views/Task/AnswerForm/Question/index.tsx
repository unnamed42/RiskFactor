import React, { forwardRef, createContext, useContext, ReactNode } from "react";

import { Form } from "antd";
import { WrappedFormUtils, GetFieldDecoratorOptions } from "antd/lib/form/Form";
import { FormItemProps } from "antd/lib/form";

import { QInput } from "./QInput";
import { QSelect } from "./QSelect";
import { QDate } from "./QDate";
import { QList } from "./QList";
import { QYesNo } from "./QYesNo";
import { QChoice } from "./QChoice";
import { QCheckbox } from "./QCheckbox";
import { QDynamic } from "./QDynamic";
import { QImmutable } from "./QImmutable";
import { QTable } from "./QTable";

import { validationRules } from "./util";

import { Question as QSchema } from "@/types/task";

import { assign } from "lodash";

import "./index.less";

const renderer = (type: QSchema["type"]) => {
  switch (type) {
    case "disabled": return QImmutable;
    case "date": return QDate;
    case "number": case "text": return QInput;
    case "select": case "select-multi": return QSelect;
    case "list": return QList;
    case "choice": return QChoice;
    case "choice-multi": return QCheckbox;
    case "either": return QYesNo;
    case "template": return QDynamic;
    case "table": return QTable;
    default: return forwardRef(() => <div />);
  }
};

export const FormContext = createContext<WrappedFormUtils | null>(null);

export { QSchema };

export interface QProps {
  schema: QSchema;
  value?: string;
  onChange?: (value: string) => void;
}

interface P extends QProps {
  formItemProps?: FormItemProps;
  decorator?: GetFieldDecoratorOptions;
  noFormItem?: boolean;
  noDecorator?: boolean;
}

export const Question = forwardRef<any, P>((props, ref) => {

  const { schema, onChange, noDecorator, noFormItem, decorator, value, children, formItemProps } = props;
  const { type, label, id } = schema;

  const { getFieldDecorator } = useContext(FormContext)!;

  const layout: FormItemProps = {
    // labelCol: { xs: { span: 24 }, sm: { span: 4 } },
    // wrapperCol: { xs: { span: 24 }, sm: { span: 20 } }
  };

  const rules = validationRules(schema);

  const Renderer = renderer(type);

  // value 属性有些需要经过setFieldValue有些（当noDecorator为true时）直接设置
  const valueProp = noDecorator ? { value } : undefined;
  let body: ReactNode = <Renderer schema={schema} ref={ref} onChange={onChange} {...valueProp} />;
  if(!noDecorator)
    body = getFieldDecorator(id.toString(), { ...rules, ...decorator})(body);

  if(noFormItem)
    return <div>{body}{children}</div>;

  return <Form.Item label={label} {...assign(layout, formItemProps)}>
    {body}
    {children}
  </Form.Item>;
});
