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

import { useForm, validationRules } from "@/utils";
import { Question as QSchema } from "@/types";

import "./index.less";

const renderer = (type: QSchema["type"]) => {
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

export const FormContext = createContext<WrappedFormUtils | null>(null);

export { QSchema };

export interface QProps {
  schema: QSchema;
  fieldPrefix?: string;
  value?: string;
  onChange?: (value: string) => void;
}

interface P extends QProps {
  formItemProps?: FormItemProps;
  decorator?: GetFieldDecoratorOptions;
  noFormItem?: boolean;
}

const layout: FormItemProps = {
  // labelCol: { xs: { span: 24 }, sm: { span: 4 } },
  // wrapperCol: { xs: { span: 24 }, sm: { span: 20 } }
};

export const Question = forwardRef<any, P>((props, ref) => {

  const { schema, onChange, noFormItem, decorator, value, formItemProps, fieldPrefix } = props;
  const { type, label, id, isEnabler, list } = schema;

  const form = useForm();

  const Renderer = renderer(type);
  const rendered = <Renderer schema={schema} ref={ref} onChange={onChange} />;

  const fieldId = fieldPrefix ? `${fieldPrefix}->$${id}` : `$${id}`;

  const body = type === "list" || type === "template" ? rendered : form.getFieldDecorator(fieldId, {
    ...validationRules(schema), initialValue: value, ...decorator
  })(rendered);

  const enabled = () => isEnabler && form.getFieldValue(fieldId) !== undefined ? <>
    {list?.map(q => <Question schema={q} noFormItem={noFormItem} fieldPrefix={`${id}`} />)}
  </> : null;
  if(isEnabler) console.log(isEnabler, id, fieldId, form.getFieldValue(fieldId));
  if(noFormItem)
    return <>{body}{enabled()}</>;

  return <Form.Item label={label} {...layout} {...formItemProps}>
    {body}
    {enabled()}
  </Form.Item>;
});
