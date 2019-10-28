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

import { validationRules } from "./util";

import "./index.less";

const renderer = (type: Question["type"]) => {
  switch (type) {
    case "NUMBER": case "TEXT": return QInput;
    case "SINGLE_SELECT": case "MULTI_SELECT": return QSelect;
    case "DATE": return QDate;
    case "LIST": return QList;
    case "LIST_APPENDABLE": return QDynamic;
    case "YESNO_CHOICE": return QYesNo;
    case "SINGLE_CHOICE": return QChoice;
    case "MULTI_CHOICE": return QCheckbox;
    default: return forwardRef(() => <div />);
  }
};

export const FormContext = createContext(null as WrappedFormUtils | null);

export interface QProps<T = any> {
  schema: Question;
  value?: T;
  onChange?: (value: T) => void;
}

interface P extends QProps {
  formItemProps?: FormItemProps;
  decorator?: GetFieldDecoratorOptions;
}

export const Question = forwardRef<any, P>(({ formItemProps, children, decorator, ...props }, ref) => {

  const { schema, onChange } = props;
  const { type, label, field } = schema;

  const { getFieldDecorator } = useContext(FormContext)!;

  const layout: FormItemProps = {
    labelCol: { xs: { span: 24 }, sm: { span: 4 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 20 } }
  };

  const rules = validationRules(schema);

  const Renderer = renderer(type);
  const child = <Renderer schema={schema} ref={ref} onChange={onChange} />;

  return <Form.Item label={label} {...layout} {...formItemProps}>
    {
      field.includes("/") && type !== "LIST" ?
        getFieldDecorator(field, { ...rules, ...decorator })(child) :
        child
    }
    {children}
  </Form.Item>;
});
