import React, { FC, useMemo } from "react";
import { get, every } from "lodash";

import { Input, Form } from "antd";
import type { FormInstance } from "rc-field-form/es";
import type { Store } from "rc-field-form/es/interface";

import type { RenderProps as P } from ".";
import { shouldUpdate } from "@/utils";

/**
 * 将表达式求值
 * @param expr 后缀表达式
 * @param form ant design form实例
 * @return 表达式求值无错误发生，且结果不为`NaN`时返回该数值，其他情况返回`undefined`
 */
const evalExpr = (expr: string, form: FormInstance): string | undefined => {
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
  return stack.length === 1 && !isNaN(stack[0]) ? stack[0].toPrecision(3): undefined;
};

/**
 * 根据所配置公式计算值，该项不可编辑
 */
export const QAutoItem: FC<P> = ({ rule: { id, placeholder } }) => {
  if (!placeholder)
    throw new Error(`自动计算项 $${id} 的计算内容配置不正确`);

  type ChangeNotifier = (prev: Store, curr: Store) => boolean;
  type ContentRenderer = (form: FormInstance) => string | undefined;

  // 根据`placeholder`代表的公式生成两个函数：
  //   其一是根据所需表单项的值计算`shouldUpdate`属性的函数
  //   其二是根据表单项计算值的函数
  const [updateChecker, renderer] = useMemo((): [ChangeNotifier, ContentRenderer] => {
    const [type, expr] = placeholder.split(":");
    if (type === "var") {
      const namePath = ["#vars", expr];
      return [shouldUpdate(expr), form => form.getFieldValue(namePath)];
    } else if (type === "expr") {
      const variables = expr.split(" ").filter(s => s.startsWith("$"));
      console.log(variables);
      return [
        (prev, curr) => every(variables, varName => get(prev, varName) == get(curr, varName)),
        form => evalExpr(expr, form)
      ];
    } else
      throw new Error(`自动计算项 $${id} 的计算公式类型不正确`);
  }, [placeholder, id]);

  // TODO: 当前无法将自动公式添加到提交内容中
  return <Form.Item shouldUpdate={updateChecker} noStyle>
    {form => <Input value={renderer(form)} disabled />}
  </Form.Item>;
};
