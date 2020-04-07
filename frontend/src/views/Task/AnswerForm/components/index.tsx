import React, { FC } from "react";

import { Form } from "antd";

import { QInput } from "./QInput";
import { QSelect } from "./QSelect";
import { QDate } from "./QDate";
import { QList } from "./QList";
import { QYesNo } from "./QYesNo";
import { QChoices } from "./QChoices";
import { QDynamic } from "./QDynamic";
import { QAutoItem } from "./QAutoItem";
import { QTable } from "./QTable";
import type { RuleInfo, RuleType } from "@/api";

import "./index.less";

export interface RenderProps {
  rule: RuleInfo;
  readonly namePath: Array<string | number>;
}

const QNothing: FC<RenderProps> = () => null;

export const renderer = (type: RuleType | undefined): FC<RenderProps> => {
  switch (type) {
    case "expression": return QAutoItem;
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

interface P extends Omit<RenderProps, "namePath"> {
  readonly namePath?: Array<number | string>;
  // 不需要用`Form.Item`包裹来增加缩进
  noStyle?: boolean;
}

/**
 * 根据规则的`type`字段选择规则的渲染器，渲染一个`Form.Item`
 */
export const Renderer: FC<P> = ({ rule, namePath, noStyle = false }) => {
  const { type, id, label } = rule;
  const Component = renderer(type);

  return <Form.Item label={label} noStyle={noStyle}>
    <Component rule={rule} namePath={pushPath(namePath, `$${id}`)} />
  </Form.Item>
};
