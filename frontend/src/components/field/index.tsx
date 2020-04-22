import React, { FC, useMemo } from "react";
import { isArray } from "lodash";

import FormItem from "antd/es/form/FormItem";
import type { NamePath } from "rc-field-form/lib/interface";

import type { RuleBase, RuleInfo } from "@/api";

import { FieldChoice } from "./FieldChoice";
import { FieldExpression } from "./FieldExpression";
import { FieldDate } from "./FieldDate";
import { FieldInput } from "./FieldInput";
import { FieldSelect } from "./FieldSelect";
import { FieldList } from "./FieldList";
import { FieldEither } from "./FieldEither";
import { FieldTable } from "./FieldTable";
import { FieldTemplate } from "./FieldTemplate";

export interface FieldProps<Rule extends RuleBase> {
  rule: Rule;
  namePath: NamePath;
}

export const extendPath = (paths: NamePath, path: string | number): Array<string | number> =>
  isArray(paths) ? [...paths, path] : [paths, path];

interface P extends FieldProps<RuleInfo> {
  noStyle?: boolean;
}

const FieldNothing: FC<FieldProps<any>> = () => null;

const fieldType = (type: RuleInfo["type"]): FC<FieldProps<any>> => {
  switch (type) {
    case "expression": return FieldExpression;
    case "date": return FieldDate;
    case "number": case "text": return FieldInput;
    case "select": case "select-multi": return FieldSelect;
    case "list": return FieldList;
    case "choice": case "choice-multi": return FieldChoice;
    case "either": return FieldEither;
    case "template": return FieldTemplate;
    case "table": return FieldTable;
    default: return FieldNothing;
  }
};

export const Field: FC<P> = ({ rule, namePath, noStyle }) => {
  const { type, id, label } = rule;
  const FieldInternal = fieldType(type);
  // TODO: 将namePath的组成由id切换为label
  const path = useMemo(() => extendPath(namePath, `$${id}`), [namePath, id]);

  return <FormItem label={label} noStyle={noStyle}>
    <FieldInternal rule={rule} namePath={path} />
  </FormItem>
};

export * from "./FieldForm";
