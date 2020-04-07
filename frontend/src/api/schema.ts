import { request, IdType } from "./base";
import type { UserInfo } from "./account";

export interface SchemaInfo {
  id: IdType;
  name: string;
  creator: UserInfo;
  modifiedAt: number;
  createdAt: number;
}

export interface SchemaDetail extends SchemaInfo {
  rules?: RuleInfo[];
}

export interface RuleInfo extends RuleAttributes {
  id: IdType;
  type?: RuleType;
  label?: string;
  list?: RuleInfo[];
}

export type RuleType =
  "root" | "header" |
  "text" | "number" | "date" | "expression" |
  "either" | "list" | "template" |
  "choice" | "choice-multi" | "select" | "select-multi" |
  "table" | "table-header";

export type RuleAttrPosition =
  "prefix" | "postfix" | null;

export interface RuleAttributes {
  required?: boolean;
  isEnabler?: boolean;
  customizable?: boolean;
  choices?: string[];
  init?: string;
  labelPosition?: RuleAttrPosition;
  addonPosition?: RuleAttrPosition;
  yesno?: string;
  description?: string;
  placeholder?: string;
}

export const getSchemas = () =>
  request<SchemaInfo[]>({ url: "/schema" });

export const schemaModifiedTime = (schemaId: IdType) =>
  request<number>({ url: `/schema/${schemaId}/modifiedAt` });

export const getSchemaInfo = (schemaId: IdType) =>
  request<SchemaInfo>({ url: `/schema/${schemaId}` });

export const getSchemaDetail = (schemaId: IdType) =>
  request<SchemaDetail>({ url: `/schema/${schemaId}/detail` });
