import React, { forwardRef, createContext } from "react";

import { Form } from "antd";
import { WrappedFormUtils, GetFieldDecoratorOptions } from "antd/lib/form/Form";
import { FormItemProps } from "antd/lib/form";

import { QInput } from "./QInput";
import { QSelect } from "./QSelect";
import { QDate } from "./QDate";
import { QList } from "./QList";
import { QYesNo } from "./QYesNo";
import { QChoices } from "./QChoices";
import { QDynamic } from "./QDynamic";
import { QImmutable } from "./QImmutable";
import { QTable } from "./QTable";

import { enablerFId, useForm, validationRules } from "@/utils";
import { Question as QSchema, QType } from "@/types";

import "./index.less";

const renderer = (type: QType | undefined) => {
  switch (type) {
    case "disabled": return QImmutable;
    case "date": return QDate;
    case "number": case "text": return QInput;
    case "select": case "select-multi": return QSelect;
    case "list": return QList;
    case "choice": case "choice-multi": return QChoices;
    case "either": return QYesNo;
    case "template": return QDynamic;
    case "table": return QTable;
    default: return forwardRef(() => <div />);
  }
};

const isCollection = (type: QType | undefined) => {
  switch (type) {
    case "list": case "template": return true;
    default: return false;
  }
};

export const FormContext = createContext<WrappedFormUtils | null>(null);

export { QSchema };

export interface QProps<T = string> {
  schema: QSchema;
  fieldPrefix?: string;
  value?: T;
  onChange?: (value: T | undefined) => void;
}

interface P<T = string> extends QProps<T> {
  formItemProps?: FormItemProps;
  decorator?: GetFieldDecoratorOptions;
  noFormItem?: boolean;
}

const layout: FormItemProps = {
  // labelCol: { xs: { span: 24 }, sm: { span: 4 } },
  // wrapperCol: { xs: { span: 24 }, sm: { span: 20 } }
};

export const Question = forwardRef<any, P<any>>(({ schema, onChange, value, ...props }, ref) => {

  const { noFormItem, decorator, formItemProps, fieldPrefix } = props;
  const { type, label, id, enabler, list, selected } = schema;

  const form = useForm();

  const Renderer = renderer(type);
  const rendered = <Renderer schema={schema} ref={ref} onChange={onChange} />;

  const fieldId = fieldPrefix ? `${fieldPrefix}.$${id}` : `$${id}`;

  const body = isCollection(type) ? rendered : form.getFieldDecorator(fieldId, {
    ...validationRules(schema), initialValue: value ?? selected, ...decorator
  })(rendered);

  const enabled = () => enabler && form.getFieldValue(fieldId) !== undefined ? <>
    {list?.map(q => <Question schema={q} key={q.id} noFormItem={noFormItem} fieldPrefix={enablerFId(id)} />)}
  </> : null;

  if(noFormItem)
    return <>{body}{enabled()}</>;

  return <Form.Item label={label} {...layout} {...formItemProps}>
    {body}
    {enabled()}
  </Form.Item>;
});
