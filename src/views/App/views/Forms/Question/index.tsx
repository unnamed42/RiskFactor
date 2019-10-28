import React, { forwardRef, createContext, useContext, PropsWithChildren } from "react";

import Form, { WrappedFormUtils } from "antd/lib/form/Form";
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

type P = PropsWithChildren<QProps & FormItemProps>;

export const Question = forwardRef<any, P>((props, ref) => {

  const { schema } = props;
  const { type, label, field } = schema;

  const Renderer = renderer(type);
  const { getFieldDecorator } = useContext(FormContext)!;

  const layout: FormItemProps = {
    labelCol: { xs: { span: 24 }, sm: { span: 4 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 20 } }
  };

  const rules = validationRules(schema);
  // antd getFieldDecorator supports nested field syntax
  // preserve slash one for Component key
  const nestedField = field.replace(/\//g, ".");

  return <Form.Item label={label} {...layout} {...(props as FormItemProps)}>
    {
      Object.keys(rules).length !== 0 ?
        getFieldDecorator(nestedField, rules)(<Renderer schema={schema} ref={ref} />) :
        <Renderer schema={schema} ref={ref} />
    }
    {props.children}
  </Form.Item>;
});
