import React, { forwardRef, createContext, useContext } from "react";

import Form, { WrappedFormUtils } from "antd/lib/form/Form";

import { QInput } from "./QInput";
import { QSelect } from "./QSelect";
import { QDate } from "./QDate";
import { QList } from "./QList";
import { QYesNo } from "./QYesNo";
import { QChoice } from "./QChoice";
import { QCheckbox } from "./QCheckbox";

import { validationRules } from "./util";

import "./index.less";

const renderer = (type: Question["type"]) => {
  switch (type) {
    case "NUMBER": case "TEXT": return QInput;
    case "SINGLE_SELECT": case "MULTI_SELECT": return QSelect;
    case "DATE": return QDate;
    case "LIST": return QList;
    case "YESNO_CHOICE": return QYesNo;
    case "SINGLE_CHOICE": return QChoice;
    case "MULTI_CHOICE": return QCheckbox;
    default: return forwardRef(() => <div />);
  }
};

export const FormContext = createContext(null as WrappedFormUtils | null);

export const Question = forwardRef<any, QProps>(({ schema, child }, ref) => {
  const Renderer = renderer(schema.type);
  const { getFieldDecorator } = useContext(FormContext)!;

  return <Form.Item label={schema.label}>
    {
      getFieldDecorator(schema.field, validationRules(schema))(
        <Renderer schema={schema} ref={ref} />
      )
    }
  </Form.Item>;
});
