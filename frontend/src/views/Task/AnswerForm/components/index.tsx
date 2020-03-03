import React, { FC } from "react";

import { Form } from "antd";
import { FormItemProps } from "antd/es/form";

import { QInput } from "./QInput";
import { QSelect } from "./QSelect";
import { QDate } from "./QDate";
import { QList } from "./QList";
import { QYesNo } from "./QYesNo";
import { QChoices } from "./QChoices";
import { QDynamic } from "./QDynamic";
import { QImmutable } from "./QImmutable";
import { QTable } from "./QTable";

import { Question } from "@/types";

import "./index.less";

export interface QProps {
  rule: Question;
  namePath: Array<string | number>;
  itemProps?: FormItemProps;
}

const QNothing: FC<QProps> = () => null;

export const renderer = (type: Question["type"]): FC<QProps> => {
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
    default: return QNothing;
  }
};

const pushPath = <T extends any>(arr: T[] | undefined, elem: T) =>
  arr ? [...arr, elem] : [elem];

interface P {
  rule: Question;
  namePath?: Array<string | number>;
  // 直接暴露内部渲染结果，不需要用`Form.Item`包裹来增加缩进
  bare?: boolean;
}

/**
 * 根据规则的`type`字段选择规则的渲染器，渲染一个`Form.Item`
 */
export const Renderer: FC<P> = ({ rule, namePath, bare }) => {
  const { type, id, label } = rule;
  const Component = renderer(type);
  const dom = <Component rule={rule} namePath={pushPath(namePath, `$${id}`)} />;
  if (bare)
    return dom;
  return <Form.Item label={label}>{dom}</Form.Item>;
};
