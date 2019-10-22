import React, { ReactElement } from "react";

import { WrappedFormUtils } from "antd/lib/form/Form";

import { qdate } from "./qdate";
import { qinput } from "./qinput";
import { qcheckbox } from "./qcheckbox";
import { qselect } from "./qselect";
import { qyesno } from "./qyesno";

export type QuestionGenerator =
  <T>(schema: Question, form: WrappedFormUtils<T>) => ReactElement;

const generator = (type: Question["type"]): QuestionGenerator => {
  switch (type) {
    case "DATE": return qdate;
    case "NUMBER": case "TEXT": return qinput;
    case "SINGLE_CHOICE": case "MULTI_CHOICE": return qcheckbox;
    case "SINGLE_SELECT": case "MULTI_SELECT": return qselect;
    case "YESNO_CHOICE": return qyesno;
    default: return (_, __) => <div />;
  }
};

/**
 * 严格说这不是Components，但是采用functional components的形式来写成组件的话，
 * 用组件语法在Form中动态创建新表项会有很多乱七八糟的边角条件和bug需要考虑
 */
export const question: QuestionGenerator = (schema, form) => {
  return generator(schema.type)(schema, form);
};
