import React, { forwardRef, createContext, useContext } from "react";

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
    case "choice": return QChoice;
    case "choice-multi": return QCheckbox;
    case "either": return QYesNo;
    case "template": return QDynamic;
    case "table": return QTable;
    default: return forwardRef(() => <div />);
  }
};

export const FormContext = createContext(null as WrappedFormUtils | null);

export { QSchema };

export interface QProps<T = any> {
  schema: QSchema;
  value?: T;
  onChange?: (value: T) => void;
}

interface P extends QProps {
  formItemProps?: FormItemProps;
  decorator?: GetFieldDecoratorOptions;
  noFormItem?: boolean;
}

export const Question = forwardRef<any, P>(({ formItemProps, noFormItem, children, decorator, ...props }, ref) => {

  const { schema, onChange } = props;
  const { type, label, id } = schema;

  const { getFieldDecorator } = useContext(FormContext)!;

  const layout: FormItemProps = {
    // labelCol: { xs: { span: 24 }, sm: { span: 4 } },
    // wrapperCol: { xs: { span: 24 }, sm: { span: 20 } }
  };

  const rules = validationRules(schema);

  const Renderer = renderer(type);
  const child = type !== "list" ?
      <Renderer schema={schema} ref={ref} onChange={onChange} /> :
      <QList list={schema.list!} ref={ref} onChange={onChange} />;

  const body = type && type !== "list" ?
      getFieldDecorator(id.toString(), { ...rules, ...decorator })(child) :
      child;

  if(noFormItem)
    return <div>{body}{children}</div>;

  return <Form.Item label={label} {...assign(layout, formItemProps)}>
    {body}
    {children}
  </Form.Item>;
});
