import { request, IdType, IdResponse, Dict } from "./base";

export interface AnswerInfo {
  id: IdType;
  creator: string;
  group: string;
  createdAt: number;
  modifiedAt: number;
}

export const getAnswer = (answerId: IdType) =>
  request<AnswerInfo>({ url: `/answers/${answerId}` });

/**
 * 获取现有的回答的列表
 */
export const getAnswerList = (schemaId: IdType) =>
  request<AnswerInfo[]>({ url: "/answers", params: { schemaId } });

export const createAnswer = (schemaId: IdType, data: Dict) =>
  request<IdResponse>({ url: "/answers", data, params: { schemaId }, method: "POST" });

export const updateAnswer = (answerId: IdType, data: Dict) =>
  request({ url: `/answers/${answerId}`, data, method: "PUT" });

export const removeAnswer = (answerId: IdType) =>
  request({ url: `/answers/${answerId}`, method: "DELETE" });
