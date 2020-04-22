import { request, IdType, IdResponse, ApiIdType } from "./base";

export interface AnswerInfo {
  id: IdType;
  creator: string;
  group: string;
  createdAt: number;
  modifiedAt: number;
}

export const getAnswer = (answerId: ApiIdType) =>
  request<any>({ url: `/answers/${answerId}` });

export const answerModifiedAt = (answerId: ApiIdType) =>
  request<number>({ url: `/answers/${answerId}/modifiedAt` });

/**
 * 获取现有的回答的列表
 */
export const getAnswerList = (schemaId: ApiIdType) =>
  request<AnswerInfo[]>({ url: "/answers", params: { schemaId } });

export const createAnswer = (schemaId: ApiIdType, data: any) =>
  request<IdResponse>({ url: "/answers", data, params: { schemaId }, method: "POST" });

export const updateAnswer = (answerId: ApiIdType, data: any) =>
  request({ url: `/answers/${answerId}`, data, method: "PUT" });

export const removeAnswer = (answerId: ApiIdType) =>
  request({ url: `/answers/${answerId}`, method: "DELETE" });
