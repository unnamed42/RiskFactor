import React, { FC, useCallback, useMemo } from "react";

import { Input, Form } from "antd";
import type { Store } from "antd/es/form/interface";
import type { FormInstance } from "rc-field-form/es";

import { fieldUpdated } from "@/hooks";
import type { RuleExpression } from "@/api";
import type { FieldProps } from ".";

/**
 * 将表达式求值
 * @param expr 后缀表达式
 * @param form ant design form实例
 * @param precision 返回字符串的表达形式的精度
 * @return 表达式求值无错误发生，且结果不为`NaN`时以字符串形式返回该数值，其他情况返回`undefined`
 */
const evalExpr = (expr: string, form: FormInstance, precision = 3): string | undefined => {
  const parseOperand = (operand: string) =>
    Number(operand.startsWith("$") ? (console.log(operand, form.getFieldValue(operand)), form.getFieldValue(operand)) : operand);
  const isDigit = (ch: number) =>
    "0".charCodeAt(0) <= ch && ch <= "9".charCodeAt(0);

  type Calculator = (lhs: number, rhs: number) => number;
  const op = (ch: string): Calculator | undefined => {
    switch (ch) {
      case "+": return (a, b) => a + b;
      case "-": return (a, b) => a - b;
      case "*": return (a, b) => a * b;
      case "/": return (a, b) => a / b;
      default:  return undefined;
    }
  };

  const stack: number[] = [];
  for (const s of expr.split(" ")) {
    if(s.length === 1 && !isDigit(s.charCodeAt(0))) {
      const rhs = stack.pop(); const lhs = stack.pop();
      const operator = op(s);
      if(operator === undefined || lhs === undefined || rhs === undefined)
        return undefined;
      stack.push(operator(lhs, rhs));
    } else
      stack.push(parseOperand(s));
  }
  return stack.length === 1 && !isNaN(stack[0]) ? stack[0].toPrecision(precision) : undefined;
};

type P = FieldProps<RuleExpression>;

/**
 * 根据所配置公式计算值，该项不可编辑
 */
export const FieldExpression: FC<P> = ({ rule: { id, placeholder } }) => {
  // if (!placeholder)
  //   throw new Error(`自动计算项 $${id} 的计算内容配置不正确`);

  type WatchList = string[] | string;
  type ContentRenderer = (form: FormInstance) => string | undefined;

  // 根据`placeholder`代表的公式生成两个函数：
  //   其一是根据所需表单项的值计算`shouldUpdate`属性的函数
  //   其二是根据表单项计算值的函数
  const [watch, renderer] = useMemo((): [WatchList, ContentRenderer] => {
    const [type, expr] = placeholder.split(":");
    if (type === "var") {
      const namePath = ["#vars", expr];
      return [expr, form => (form.getFieldValue(namePath) as string | undefined)];
    } else if (type === "expr") {
      const variables = expr.split(" ").filter(s => s.startsWith("$"));
      return [variables, form => evalExpr(expr, form)];
    } else
      throw new Error(`自动计算项 $${id} 的计算公式类型不正确`);
  }, [placeholder, id]);

  const updated = useCallback((prev: Store, curr: Store) => {
    if(typeof watch === "string")
      return fieldUpdated(watch)(prev, curr);
    else
      return watch.some(w => fieldUpdated(w)(prev, curr));
  }, [watch]);

  // TODO: 当前无法将自动公式添加到提交内容中
  return <Form.Item shouldUpdate={updated} noStyle>
    {form => <Input value={renderer(form)} disabled />}
  </Form.Item>;
};
